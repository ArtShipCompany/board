import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { Line, Point, Board } from '../types/drawing';
import { boardApi } from '../api/boardApi';
import { sessionApi } from '../api/sessionApi';
import { strokeApi } from '../api/strokeApi';

interface DrawingState {
  lines: Line[];
  strokeIds: number[];
  sessionId: number | null;
  currentLine: Line | null;
  color: string;
  brushSize: number;
  tool: 'brush' | 'eraser';
  status: 'idle' | 'loading' | 'failed';
  pendingStrokes: { line: Line; tempId: string }[];
  board: Board | null;
}

const initialState: DrawingState = {
  lines: [],
  strokeIds: [],
  sessionId: null,
  currentLine: null,
  color: '#000000',
  brushSize: 5,
  tool: 'brush',
  status: 'idle',
  pendingStrokes: [],
  board: null,
};

function pointsToKonvaPoints(points: any): number[] {
  if (!points) return [];

  if (typeof points === 'string') {
    try {
      points = JSON.parse(points);
    } catch {
      return [];
    }
  }

  if (Array.isArray(points) && points.length > 0 && typeof points[0] === 'number') {
    return points;
  }

  if (Array.isArray(points)) {
    return points.flatMap((p) => [Number(p.x), Number(p.y)]);
  }

  return [];
}

export const initBoard = createAsyncThunk('drawing/initBoard', async (boardId: number) => {
  const board = await boardApi.getBoardById(boardId);

  if (!board) {
    throw new Error('Доска не найдена');
  }

  let session = await sessionApi.getActiveSessionByBoard(board.id);

  if (!session) {
    session = await sessionApi.createSession({
      boardId: board.id,
      userId: 1,
      layerId: 1,
    });
  }

  const strokes = await strokeApi.getStrokesBySession(session.id);

  const lines: Line[] = strokes.map((s) => ({
    points: pointsToKonvaPoints(s.points) as any,
    color: s.color,
    width: s.size,
    type: 'brush',
  }));

  return {
    lines,
    strokeIds: strokes.map((s) => s.id),
    sessionId: session.id,
    board,
  };
});

export const undoLastStroke = createAsyncThunk(
  'drawing/undoLastStroke',
  async (_, { getState, dispatch }) => {
    const state = getState() as any;
    const { strokeIds } = state.drawing;

    if (strokeIds.length === 0) return;

    const lastStrokeId = strokeIds[strokeIds.length - 1];

    try {
      await strokeApi.deleteStroke(lastStrokeId);
    } catch (err) {
      console.warn(`[Undo] Не удалось удалить штрих ${lastStrokeId} на сервере (возможно, его там нет), удаляем локально.`);
    } finally {
      dispatch(drawingSlice.actions.localUndo());
    }
  }
);

export const clearCanvas = createAsyncThunk(
  'drawing/clearCanvas',
  async (_, { getState, dispatch }) => {
    const state = getState() as any;
    const { sessionId } = state.drawing;

    if (!sessionId) return;

    try {
      dispatch(drawingSlice.actions.localClear());
    } catch (err) {
      console.error('Не удалось очистить холст:', err);
    }
  }
);

const drawingSlice = createSlice({
  name: 'drawing',
  initialState,
  reducers: {
    resetDrawing: (state) => {
      state.lines = [];
      state.strokeIds = [];
      state.currentLine = null;
      state.sessionId = null;
      state.board = null;
    },
    setTool: (state, action: PayloadAction<'brush' | 'eraser'>) => {
      state.tool = action.payload;
    },
    setColor: (state, action: PayloadAction<string>) => {
      state.color = action.payload;
    },
    setBrushSize: (state, action: PayloadAction<number>) => {
      state.brushSize = action.payload;
    },
    startDrawing: (state, action: PayloadAction<Point>) => {
      state.currentLine = {
        points: [action.payload.x, action.payload.y] as any,
        color: state.tool === 'eraser' ? '#ffffff' : state.color,
        width: state.brushSize,
        type: state.tool,
      };
    },
    draw: (state, action: PayloadAction<Point>) => {
      if (state.currentLine) {
        state.currentLine.points = [...(state.currentLine.points as any), action.payload.x, action.payload.y];
      }
    },
    stopDrawing: (state) => {
      state.currentLine = null;
    },
    addSavedStroke: (state, action: PayloadAction<{ line: Line, id: number }>) => {
      state.lines.push(action.payload.line);
      state.strokeIds.push(action.payload.id);
    },
    localUndo: (state) => {
      state.lines.pop();
      state.strokeIds.pop();
    },
    localClear: (state) => {
      state.lines = [];
      state.strokeIds = [];
      state.currentLine = null;
    },
    queueStroke: (state, action: PayloadAction<{ line: Line; tempId: string }>) => {
      state.pendingStrokes.push(action.payload);
    },
    removeFromQueue: (state, action: PayloadAction<string>) => {
      state.pendingStrokes = state.pendingStrokes.filter(s => s.tempId !== action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(initBoard.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(initBoard.fulfilled, (state, action) => {
        state.lines = action.payload.lines;
        state.strokeIds = action.payload.strokeIds;
        state.sessionId = action.payload.sessionId;
        state.board = action.payload.board;
        state.status = 'idle';
      })
      .addCase(initBoard.rejected, (state, action) => {
        state.status = 'failed';
        console.error('[drawingSlice] initBoard failed:', action.error);
      })
  }
});

export const syncPendingStrokes = createAsyncThunk(
  'drawing/syncPendingStrokes',
  async (_, { getState, dispatch }) => {
    const state = getState() as any;
    const { sessionId, pendingStrokes } = state.drawing;

    if (!sessionId || !navigator.onLine || pendingStrokes.length === 0) return;

    for (const stroke of pendingStrokes) {
      try {
        const pointsArray = stroke.line.points as unknown as number[];
        const formattedPoints: Point[] = [];
        for (let i = 0; i < pointsArray.length; i += 2) {
          formattedPoints.push({ x: pointsArray[i], y: pointsArray[i + 1] });
        }

        const saved = await strokeApi.createStroke({
          sessionId,
          layerId: 1,
          color: stroke.line.color,
          size: stroke.line.width,
          points: formattedPoints,
        });

        dispatch(addSavedStroke({ line: stroke.line, id: saved.id }));
        dispatch(removeFromQueue(stroke.tempId));
      } catch (err) {
        console.warn('Не удалось синхронизировать штрих:', err);
      }
    }
  }
);

export const { setTool, setColor, setBrushSize, startDrawing, draw, stopDrawing, addSavedStroke, resetDrawing, queueStroke, removeFromQueue } = drawingSlice.actions;
export default drawingSlice.reducer;