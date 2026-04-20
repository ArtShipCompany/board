import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { Line, Point } from '../types/drawing';
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
};

export const initBoard = createAsyncThunk('drawing/initBoard', async () => {
  const board = await boardApi.getMyBoard();
  let session = await sessionApi.getActiveSessionByBoard(board.id);
  if (!session) {
    session = await sessionApi.createSession({ boardId: board.id, userId: 1, layerId: 1 });
  }
  const strokes = await strokeApi.getStrokesBySession(session.id);
  const lines: Line[] = strokes.map(s => ({
    points: JSON.parse(s.points),
    color: s.color,
    width: s.size,
    type: 'brush',
  }));
  return { lines, strokeIds: strokes.map(s => s.id), sessionId: session.id };
});

const drawingSlice = createSlice({
  name: 'drawing',
  initialState,
  reducers: {
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
    undo: (state) => {
      state.lines.pop();
      state.strokeIds.pop();
    },
    clear: (state) => {
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
    builder.addCase(initBoard.fulfilled, (state, action) => {
      state.lines = action.payload.lines;
      state.strokeIds = action.payload.strokeIds;
      state.sessionId = action.payload.sessionId;
      state.status = 'idle';
    });
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

export const { setTool, setColor, setBrushSize, startDrawing, draw, stopDrawing, addSavedStroke, undo, clear, queueStroke, removeFromQueue  } = drawingSlice.actions;
export default drawingSlice.reducer;