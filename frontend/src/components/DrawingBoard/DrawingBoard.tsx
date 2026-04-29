import React, { useEffect, useRef, useState } from 'react';
import { Stage, Layer, Line as KonvaLine, Circle as KonvaCircle } from 'react-konva';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { startDrawing, draw, stopDrawing, addSavedStroke, initBoard, queueStroke, syncPendingStrokes } from '../../store/drawingSlice';
import { strokeApi } from '../../api/strokeApi';
import { useNetworkStatus } from '../../hooks/useNetworkStatus';
import './DrawingBoard.css';

const DrawingBoard: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { lines, currentLine, sessionId, brushSize, tool, color } = useSelector((state: RootState) => state.drawing);
  const isOnline = useNetworkStatus();
  const containerRef = useRef<HTMLDivElement>(null);
  const [dimensions, setDimensions] = useState({ width: 800, height: 600 });
  const [cursorPos, setCursorPos] = useState<{ x: number, y: number } | null>(null);

  useEffect(() => {
    dispatch(initBoard());
  }, [dispatch]);

  useEffect(() => {
    if (containerRef.current) {
      const { width, height } = containerRef.current.getBoundingClientRect();
      setDimensions({ width, height });
    }
  }, []);

  const handleMouseDown = (e: any) => {
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();
    dispatch(startDrawing(pos));
  };

  const handleMouseMove = (e: any) => {
    if (e.evt.buttons === 0 && !e.target.getStage().getPointerPosition()) return;
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();
    if (pos) {
      dispatch(draw(pos));
      setCursorPos(pos);
    }
  };

  const handleMouseUp = async () => {
    console.log('[DrawingBoard] mouse up:', {
      currentLine,
      sessionId,
      isOnline,
    });
    
    if (!currentLine || !sessionId) {
      dispatch(stopDrawing());
      return;
    }

    const tempId = crypto.randomUUID();
    const strokePayload = { line: currentLine, tempId };

    if (isOnline) {
      try {
        const pointsArray = currentLine.points as unknown as number[];
        const formattedPoints = [];
        for (let i = 0; i < pointsArray.length; i += 2) {
          formattedPoints.push({ x: pointsArray[i], y: pointsArray[i + 1] });
        }
        const saved = await strokeApi.createStroke({
          sessionId,
          layerId: 1,
          color: currentLine.color,
          size: currentLine.width,
          points: formattedPoints,
        });
        dispatch(addSavedStroke({ line: currentLine, id: saved.id }));
        dispatch(syncPendingStrokes());
      } catch {
        dispatch(queueStroke(strokePayload));
      }
    } else {
      dispatch(queueStroke(strokePayload));
    }

    dispatch(stopDrawing());
  };

  return (
    <div ref={containerRef} className="drawing-board-container">
      <Stage
        width={dimensions.width}
        height={dimensions.height}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onTouchStart={handleMouseDown}
        onTouchMove={handleMouseMove}
        onTouchEnd={handleMouseUp}
      >
        <Layer>
          {lines.map((line, i) => (
            <KonvaLine
              key={i}
              points={line.points as any}
              stroke={line.color}
              strokeWidth={line.width}
              tension={0.5}
              lineCap="round"
              lineJoin="round"
              globalCompositeOperation={
                line.type === 'eraser' ? 'destination-out' : 'source-over'
              }
            />
          ))}

          {currentLine && (
            <KonvaLine
              points={currentLine.points as any}
              stroke={currentLine.color}
              strokeWidth={currentLine.width}
              tension={0.5}
              lineCap="round"
              lineJoin="round"
              globalCompositeOperation={
                currentLine.type === 'eraser' ? 'destination-out' : 'source-over'
              }
            />
          )}
          {cursorPos && (
            <KonvaCircle
              x={cursorPos.x}
              y={cursorPos.y}
              radius={brushSize / 2}
              fill={tool === 'eraser' ? 'transparent' : color}
              stroke={tool === 'eraser' ? '#000' : color}
              strokeWidth={tool === 'eraser' ? 2 : 0}
              opacity={0.4}
              dash={tool === 'eraser' ? [4, 4] : undefined}
              listening={false}
            />
          )}
        </Layer>
      </Stage>
    </div>
  );
};

export default DrawingBoard;