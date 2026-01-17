import React, { createContext, useContext, useReducer } from 'react'

const initialState = {
  lines: [],
  currentLine: null,
  color: '#000000',
  brushSize: 5,
  tool: 'brush',
  isDrawing: false,
}

const drawingReducer = (state, action) => {
  switch (action.type) {
    case 'START_DRAWING':
      const newLine = {
        points: [action.payload],
        color: state.tool === 'eraser' ? '#ffffff' : state.color,
        width: state.brushSize,
        type: state.tool,
      }
      return {
        ...state,
        lines: [...state.lines],
        currentLine: newLine,
        isDrawing: true,
      }
      
    case 'DRAW':
      if (!state.currentLine) return state
      const updatedLine = {
        ...state.currentLine,
        points: [...state.currentLine.points, action.payload],
      }
      return {
        ...state,
        currentLine: updatedLine,
        lines: [...state.lines],
      }
      
    case 'STOP_DRAWING':
      if (!state.currentLine) return { ...state, isDrawing: false }
      return {
        ...state,
        lines: [...state.lines, state.currentLine],
        currentLine: null,
        isDrawing: false,
      }
      
    case 'SET_COLOR':
      return { ...state, color: action.payload }
      
    case 'SET_BRUSH_SIZE':
      return { ...state, brushSize: action.payload }
      
    case 'SET_TOOL':
      return { ...state, tool: action.payload }
      
    case 'CLEAR_CANVAS':
      return { ...state, lines: [], currentLine: null }
      
    case 'UNDO':
      return { ...state, lines: state.lines.slice(0, -1) }
      
    default:
      return state
  }
}

const DrawingContext = createContext()

export const useDrawing = () => {
  const context = useContext(DrawingContext)
  if (!context) {
    throw new Error('useDrawing must be used within a DrawingProvider')
  }
  return context
}

export const DrawingProvider = ({ children }) => {
  const [state, dispatch] = useReducer(drawingReducer, initialState)
  
  return (
    <DrawingContext.Provider value={{ state, dispatch }}>
      {children}
    </DrawingContext.Provider>
  )
}