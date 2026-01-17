import React, { useRef, useEffect, useCallback } from 'react'
import { useDrawing } from '../../hooks/useDrawing'
import { redrawCanvas, drawLine } from '../../utils/drawingUtils'
import './DrawingBoard.css'

const DrawingBoard = () => {
  const canvasRef = useRef(null)
  const containerRef = useRef(null)
  const { state, dispatch } = useDrawing()
  
  const getCanvasCoordinates = useCallback((clientX, clientY) => {
    const canvas = canvasRef.current
    if (!canvas) return { x: 0, y: 0 }
    
    const rect = canvas.getBoundingClientRect()
    return {
      x: clientX - rect.left,
      y: clientY - rect.top,
    }
  }, [])
  
  const handleMouseDown = (e) => {
    const point = getCanvasCoordinates(e.clientX, e.clientY)
    dispatch({ type: 'START_DRAWING', payload: point })
  }
  
  const handleMouseMove = (e) => {
    if (!state.isDrawing || !state.currentLine) return
    
    const point = getCanvasCoordinates(e.clientX, e.clientY)
    dispatch({ type: 'DRAW', payload: point })
    
    const canvas = canvasRef.current
    const ctx = canvas?.getContext('2d')
    if (ctx && state.currentLine.points.length > 1) {
      const lastPoint = state.currentLine.points[state.currentLine.points.length - 2]
      drawLine(ctx, state.currentLine, lastPoint)
    }
  }
  
  const handleMouseUp = () => {
    if (state.isDrawing) {
      dispatch({ type: 'STOP_DRAWING' })
    }
  }
  
  const handleTouchStart = (e) => {
    e.preventDefault()
    const touch = e.touches[0]
    const point = getCanvasCoordinates(touch.clientX, touch.clientY)
    dispatch({ type: 'START_DRAWING', payload: point })
  }
  
  const handleTouchMove = (e) => {
    e.preventDefault()
    if (!state.isDrawing || !state.currentLine) return
    
    const touch = e.touches[0]
    const point = getCanvasCoordinates(touch.clientX, touch.clientY)
    dispatch({ type: 'DRAW', payload: point })
    
    const canvas = canvasRef.current
    const ctx = canvas?.getContext('2d')
    if (ctx && state.currentLine.points.length > 1) {
      const lastPoint = state.currentLine.points[state.currentLine.points.length - 2]
      drawLine(ctx, state.currentLine, lastPoint)
    }
  }
  
  useEffect(() => {
    const canvas = canvasRef.current
    const container = containerRef.current
    if (!canvas || !container) return
    
    const resizeCanvas = () => {
      const { width, height } = container.getBoundingClientRect()
      canvas.width = width
      canvas.height = height
      
      const ctx = canvas.getContext('2d')
      if (ctx) {
        redrawCanvas(ctx, canvas, state.lines)
      }
    }
    
    resizeCanvas()
    
    const ctx = canvas.getContext('2d')
    if (ctx) {
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      redrawCanvas(ctx, canvas, state.lines)
    }
    
    const resizeObserver = new ResizeObserver(resizeCanvas)
    resizeObserver.observe(container)
    
    return () => {
      resizeObserver.disconnect()
    }
  }, [state.lines])
  
  return (
    <div ref={containerRef} className="drawing-board-container">
      <canvas
        ref={canvasRef}
        className="drawing-canvas"
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleMouseUp}
      />
    </div>
  )
}

export default DrawingBoard