import React, { useRef, useEffect, useCallback, useState } from 'react';
import { useDrawing } from '../../hooks/useDrawing';
import { redrawCanvas, drawLine, clearCanvas } from '../../utils/drawingUtils';
import './DrawingBoard.css';

const DrawingBoard = () => {
  const canvasRef = useRef(null);
  const previewCanvasRef = useRef(null);
  const containerRef = useRef(null);
  const { state, dispatch } = useDrawing();
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
  const [isMouseOverCanvas, setIsMouseOverCanvas] = useState(false);

  const getCanvasCoordinates = useCallback((clientX, clientY) => {
    const canvas = canvasRef.current;
    if (!canvas) return { x: 0, y: 0 };
    const rect = canvas.getBoundingClientRect();
    return {
      x: clientX - rect.left,
      y: clientY - rect.top,
    };
  }, []);

  const redrawMainCanvas = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    clearCanvas(ctx, canvas);
    state.lines.forEach(line => {
      drawLine(ctx, line);
    });
    if (state.isDrawing && state.currentLine && state.currentLine.points.length > 1) {
      drawLine(ctx, state.currentLine);
    }
  }, [state.lines, state.isDrawing, state.currentLine]);

  const drawPreview = useCallback(() => {
    const previewCanvas = previewCanvasRef.current;
    if (!previewCanvas || !isMouseOverCanvas) return;
    const ctx = previewCanvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, previewCanvas.width, previewCanvas.height);
    if (state.tool === 'brush' && state.isDrawing) return;

    const pos = mousePosition;
    const brushSize = state.brushSize;
    ctx.beginPath();
    ctx.arc(pos.x, pos.y, brushSize / 2, 0, Math.PI * 2);
    if (state.tool === 'eraser') {
      ctx.fillStyle = '#ffffff';
      ctx.strokeStyle = '#94a3b8';
      ctx.lineWidth = 2;
      ctx.fill();
      ctx.stroke();
    } else {
      ctx.fillStyle = state.color + '80';
      ctx.fill();
    }
    ctx.closePath();
  }, [mousePosition, state.brushSize, state.color, state.tool, state.isDrawing, isMouseOverCanvas]);

  useEffect(() => {
    drawPreview();
  }, [drawPreview]);

  useEffect(() => {
    redrawMainCanvas();
  }, [redrawMainCanvas]);

  const handleMouseDown = (e) => {
    const point = getCanvasCoordinates(e.clientX, e.clientY);
    dispatch({ type: 'START_DRAWING', payload: point });
  };

  const handleMouseMove = (e) => {
    const point = getCanvasCoordinates(e.clientX, e.clientY);
    setMousePosition(point);
    if (state.isDrawing && state.currentLine) {
      dispatch({ type: 'DRAW', payload: point });
    }
    drawPreview();
  };

  const handleMouseUp = () => {
    if (state.isDrawing) {
      dispatch({ type: 'STOP_DRAWING' });
    }
    setTimeout(() => drawPreview(), 0);
  };

  const handleTouchStart = (e) => {
    e.preventDefault();
    const touch = e.touches[0];
    const point = getCanvasCoordinates(touch.clientX, touch.clientY);
    dispatch({ type: 'START_DRAWING', payload: point });
  };

  const handleTouchMove = (e) => {
    e.preventDefault();
    if (!state.isDrawing || !state.currentLine) return;
    const touch = e.touches[0];
    const point = getCanvasCoordinates(touch.clientX, touch.clientY);
    dispatch({ type: 'DRAW', payload: point });
  };

  useEffect(() => {
    const mainCanvas = canvasRef.current;
    const previewCanvas = previewCanvasRef.current;
    const container = containerRef.current;
    if (!mainCanvas || !previewCanvas || !container) return;

    const resizeCanvas = () => {
      const { width, height } = container.getBoundingClientRect();
      mainCanvas.width = width;
      mainCanvas.height = height;
      previewCanvas.width = width;
      previewCanvas.height = height;
      previewCanvas.style.width = `${width}px`;
      previewCanvas.style.height = `${height}px`;
      redrawMainCanvas();
    };

    resizeCanvas();
    const resizeObserver = new ResizeObserver(resizeCanvas);
    resizeObserver.observe(container);
    return () => {
      resizeObserver.disconnect();
    };
  }, [redrawMainCanvas]);

  const handleMouseEnter = () => {
    setIsMouseOverCanvas(true);
    drawPreview();
  };

  const handleMouseLeave = () => {
    setIsMouseOverCanvas(false);
    const previewCanvas = previewCanvasRef.current;
    if (previewCanvas) {
      const ctx = previewCanvas.getContext('2d');
      if (ctx) {
        ctx.clearRect(0, 0, previewCanvas.width, previewCanvas.height);
      }
    }
    if (state.isDrawing) {
      dispatch({ type: 'STOP_DRAWING' });
    }
  };

  useEffect(() => {
    drawPreview();
  }, [state.tool, state.brushSize, state.color, drawPreview]);

  return (
    <div ref={containerRef} className="drawing-board-container">
      <canvas
        ref={canvasRef}
        className="drawing-canvas main-canvas"
      />
      <canvas
        ref={previewCanvasRef}
        className="drawing-canvas preview-canvas"
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseLeave}
        onMouseEnter={handleMouseEnter}
        onTouchStart={handleTouchStart}
        onTouchMove={handleTouchMove}
        onTouchEnd={handleMouseUp}
      />
    </div>
  );
};

export default DrawingBoard;