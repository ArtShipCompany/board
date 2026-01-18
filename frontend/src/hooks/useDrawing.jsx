import React, { createContext, useContext, useReducer, useEffect, useRef } from 'react';
import { sessionApi } from '../api/sessionApi';
import { strokeApi } from '../api/strokeApi';
import { boardApi } from '../api/boardApi';


const initialState = {
  lines: [],
  strokeIds: [],
  sessionId: null,
  currentLine: null,
  color: '#000000',
  brushSize: 5,
  tool: 'brush',
  isDrawing: false,
  loaded: false,
};


const drawingReducer = (state, action) => {
  switch (action.type) {
    case 'LOAD_SESSION':
      return {
        ...state,
        lines: action.payload.lines,
        strokeIds: action.payload.strokeIds,
        sessionId: action.payload.sessionId,
        loaded: true,
      };


    case 'ADD_STROKE':
      return {
        ...state,
        lines: [...state.lines, action.payload.stroke],
        strokeIds: [...state.strokeIds, action.payload.strokeId],
      };


    case 'REMOVE_LAST_STROKE':
      return {
        ...state,
        lines: state.lines.slice(0, -1),
        strokeIds: state.strokeIds.slice(0, -1),
      };


    case 'START_DRAWING':
      const newLine = {
        points: [action.payload],
        color: state.tool === 'eraser' ? '#ffffff' : state.color,
        width: state.brushSize,
        type: state.tool,
      };
      return {
        ...state,
        currentLine: newLine,
        isDrawing: true,
      };


    case 'DRAW':
      if (!state.currentLine) return state;
      return {
        ...state,
        currentLine: {
          ...state.currentLine,
          points: [...state.currentLine.points, action.payload],
        },
      };


    case 'STOP_DRAWING':
      return {
        ...state,
        isDrawing: false,
      };


    case 'SET_COLOR':
      return { ...state, color: action.payload };


    case 'SET_BRUSH_SIZE':
      return { ...state, brushSize: action.payload };


    case 'SET_TOOL':
      return { ...state, tool: action.payload };


    case 'CLEAR_CANVAS':
      return { ...state, lines: [], strokeIds: [], currentLine: null };


    default:
      return state;
  }
};


const DrawingContext = createContext();


export const useDrawing = () => {
  const context = useContext(DrawingContext);
  if (!context) {
    throw new Error('useDrawing must be used within a DrawingProvider');
  }
  return context;
};


export const DrawingProvider = ({ children }) => {
  const [state, dispatch] = useReducer(drawingReducer, initialState);
  const linesRef = useRef(state.lines);
  const strokeIdsRef = useRef(state.strokeIds);
  const sessionIdRef = useRef(state.sessionId);


  useEffect(() => {
    linesRef.current = state.lines;
    strokeIdsRef.current = state.strokeIds;
    sessionIdRef.current = state.sessionId;
  }, [state.lines, state.strokeIds, state.sessionId]);


  useEffect(() => {
    const init = async () => {
      try {
        const board = await boardApi.getMyBoard();


        let session = await sessionApi.getActiveSessionByBoard(board.id);
        if (!session) {
          session = await sessionApi.createSession({
            boardId: board.id,
            userId: 1,
            layerId: 1,
          });
        }


        const strokes = await strokeApi.getStrokesBySession(session.id);
        const lines = strokes.map((s) => ({
          points: JSON.parse(s.points),
          color: s.color,
          width: s.size,
          type: 'brush',
        }));
        const strokeIds = strokes.map((s) => s.id);


        dispatch({
          type: 'LOAD_SESSION',
          payload: { lines, strokeIds, sessionId: session.id },
        });
      } catch (err) {
        console.error('Ошибка инициализации:', err);
      }
    };


    init();
  }, []);


  useEffect(() => {
    if (
      !state.isDrawing &&
      state.currentLine &&
      state.loaded &&
      sessionIdRef.current
    ) {
      const saveStroke = async () => {
        try {
          const saved = await strokeApi.createStroke({
            sessionId: sessionIdRef.current,
            layerId: 1,
            color: state.currentLine.color,
            size: state.currentLine.width,
            points: state.currentLine.points,
          });


          dispatch({
            type: 'ADD_STROKE',
            payload: { stroke: state.currentLine, strokeId: saved.id },
          });
        } catch (err) {
          console.error('Ошибка при сохранении штриха:', err);
        }
      };


      saveStroke();
    }
  }, [state.isDrawing, state.currentLine, state.loaded]);


  const handleClearCanvas = () => {
    dispatch({ type: 'CLEAR_CANVAS' });
  };


  const handleUndo = async () => {
    if (strokeIdsRef.current.length === 0) return;
    const lastId = strokeIdsRef.current[strokeIdsRef.current.length - 1];
    try {
      await strokeApi.deleteStroke(lastId);
      dispatch({ type: 'REMOVE_LAST_STROKE' });
    } catch (err) {
      console.error('Ошибка отмены:', err);
    }
  };


  return (
    <DrawingContext.Provider
      value={{
        state,
        dispatch,
        handleUndo,
        handleClearCanvas,
      }}
    >
      {children}
    </DrawingContext.Provider>
  );
};