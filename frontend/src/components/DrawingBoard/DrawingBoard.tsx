import React, { useEffect, useMemo, useRef, useState } from 'react';
import {
  Stage,
  Layer,
  Line as KonvaLine,
  Circle as KonvaCircle,
  Text as KonvaText,
} from 'react-konva';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import {
  startDrawing,
  draw,
  stopDrawing,
  addSavedStroke,
  initBoard,
  queueStroke,
  syncPendingStrokes,
} from '../../store/drawingSlice';
import { strokeApi } from '../../api/strokeApi';
import {
  connectDrawingSocket,
  sendStroke,
  sendCursor,
  DrawingSocketEvent,
} from '../../api/drawingSocket';
import { useNetworkStatus } from '../../hooks/useNetworkStatus';
import './DrawingBoard.css';

type RemoteCursor = {
  visitorId: string;
  x: number;
  y: number;
  color: string;
};

const ROOM_ID = '1';

function createClientId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }

  return `visitor-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function linePointsToPointObjects(points: number[]) {
  const result: { x: number; y: number }[] = [];

  for (let i = 0; i < points.length; i += 2) {
    result.push({
      x: points[i],
      y: points[i + 1],
    });
  }

  return result;
}

function makeVisitorColor(visitorId: string) {
  const colors = ['#ff3366', '#3366ff', '#22aa66', '#ff9900', '#aa33ff', '#00aacc'];
  let sum = 0;

  for (const char of visitorId) {
    sum += char.charCodeAt(0);
  }

  return colors[sum % colors.length];
}

const DrawingBoard: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();

  const { lines, currentLine, sessionId, brushSize, tool, color } = useSelector(
    (state: RootState) => state.drawing
  );

  const isOnline = useNetworkStatus();
  const containerRef = useRef<HTMLDivElement>(null);
  const lastCursorSentAtRef = useRef<number>(0);

  const [dimensions, setDimensions] = useState({ width: 800, height: 600 });
  const [cursorPos, setCursorPos] = useState<{ x: number; y: number } | null>(null);
  const [remoteCursors, setRemoteCursors] = useState<Record<string, RemoteCursor>>({});

  const [scale, setScale] = useState(1);

  const [isSpacePressed, setIsSpacePressed] = useState(false);
  const [panOffset, setPanOffset] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const lastMousePosRef = useRef({ x: 0, y: 0 });

  const visitorId = useMemo(() => {
    const existing = localStorage.getItem('visitorId');

    if (existing) {
      return existing;
    }

    const created = createClientId();
    localStorage.setItem('visitorId', created);
    return created;
  }, []);

  const visitorName = useMemo(() => {
    const existing = localStorage.getItem('visitorName');

    if (existing) {
      return existing;
    }

    const created = `User-${visitorId.slice(0, 4)}`;
    localStorage.setItem('visitorName', created);
    return created;
  }, [visitorId]);

  const visitorColor = useMemo(() => {
    const existing = localStorage.getItem('visitorColor');

    if (existing) {
      return existing;
    }

    const created = makeVisitorColor(visitorId);
    localStorage.setItem('visitorColor', created);
    return created;
  }, [visitorId]);

  useEffect(() => {
    dispatch(initBoard());
  }, [dispatch]);

  useEffect(() => {
    const updateDimensions = () => {
      if (!containerRef.current) {
        return;
      }

      const { width, height } = containerRef.current.getBoundingClientRect();

      setDimensions({
        width: width || 800,
        height: height || 600,
      });
    };

    updateDimensions();
    window.addEventListener('resize', updateDimensions);

    return () => {
      window.removeEventListener('resize', updateDimensions);
    };
  }, []);

  useEffect(() => {
    const disconnect = connectDrawingSocket({
      roomId: ROOM_ID,
      visitorId,
      visitorName,
      color: visitorColor,

      onEvent: (event: DrawingSocketEvent) => {
        if (event.visitorId === visitorId) {
          return;
        }

        if (event.type === 'room_full') {
          alert('Комната заполнена. Максимум 10 участников.');
          return;
        }

        if (event.type === 'stroke_draw') {
          const points = event.points || [];
          const konvaPoints = points.flatMap((p) => [p.x, p.y]);

          dispatch(
            addSavedStroke({
              id: event.strokeId || Date.now(),
              line: {
                points: konvaPoints as any,
                color: event.color || '#000000',
                width: event.size || 5,
                type: 'brush',
              },
            })
          );

          return;
        }

        if (event.type === 'cursor_update') {
          if (!event.visitorId || typeof event.x !== 'number' || typeof event.y !== 'number') {
            return;
          }

          setRemoteCursors((prev) => ({
            ...prev,
            [event.visitorId as string]: {
              visitorId: event.visitorId as string,
              x: event.x as number,
              y: event.y as number,
              color: event.color || '#ff3366',
            },
          }));

          return;
        }

        if (event.type === 'visitor_left' && event.visitorId) {
          setRemoteCursors((prev) => {
            const copy = { ...prev };
            delete copy[event.visitorId as string];
            return copy;
          });

          return;
        }

        console.log('[DrawingBoard] WS event:', event);
      },
    });

    return () => {
      disconnect();
    };
  }, [dispatch, visitorId, visitorName, visitorColor]);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.code === 'Space') {
        setIsSpacePressed(true);
        (document.activeElement as HTMLElement)?.blur();
      }
      if (event.key === '+' || event.key === '=') {
        setScale(prev => Math.min(prev + 0.1, 3));
      } 
      else if (event.key === '-') {
        setScale(prev => Math.max(prev - 0.1, 0.3));
      }
    };

    const handleKeyUp = (e: KeyboardEvent) => {
      if (e.code === 'Space') {
        setIsSpacePressed(false);
        setIsDragging(false);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, []);

  const handleMouseDown = (e: any) => {
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();

    if (!pos) {
      return;
    }

    if (isSpacePressed) {
      setIsDragging(true);
      lastMousePosRef.current = pos;
      return;
    }

    const realPos = {
      x: (pos.x - panOffset.x) / scale,
      y: (pos.y - panOffset.y) / scale
    };
    dispatch(startDrawing(realPos));
  };

  const handleMouseMove = (e: any) => {
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();
    const clamp = (value: number, min: number, max: number) => Math.min(Math.max(value, min), max);


    if (!pos) {
      return;
    }

    if (isSpacePressed && isDragging) {
      const dx = pos.x - lastMousePosRef.current.x;
      const dy = pos.y - lastMousePosRef.current.y;
      
      const nextX = panOffset.x + dx;
      const nextY = panOffset.y + dy;

      setPanOffset({
        x: clamp(nextX, -dimensions.width * scale, dimensions.width),
        y: clamp(nextY, -dimensions.height * scale, dimensions.height)
      });
      
      lastMousePosRef.current = pos;
      return;
    }

    if (e.evt.buttons !== 0 && !isSpacePressed) {
      const realPos = {
        x: (pos.x - panOffset.x) / scale,
        y: (pos.y - panOffset.y) / scale
      };
      dispatch(draw(realPos));
    }

    setCursorPos({
        x: pos.x,
        y: pos.y
    });

    const now = Date.now();

    if (now - lastCursorSentAtRef.current > 75) {
      lastCursorSentAtRef.current = now;

      sendCursor({
        roomId: ROOM_ID,
        visitorId,
        x: (pos.x - panOffset.x) / scale,
        y: (pos.y - panOffset.y) / scale,
        color: visitorColor,
      });
    }
  };

  const handleMouseUp = async () => {
    setIsDragging(false);

    console.log('[DrawingBoard] mouse up:', {
      currentLine,
      sessionId,
      isOnline,
    });

    if (!currentLine || !sessionId) {
      dispatch(stopDrawing());
      return;
    }

    const tempId = createClientId();
    const strokePayload = { line: currentLine, tempId };

    try {
      if (!isOnline) {
        dispatch(queueStroke(strokePayload));
        return;
      }

      const pointsArray = currentLine.points as unknown as number[];
      const formattedPoints = linePointsToPointObjects(pointsArray);

      if (formattedPoints.length < 2) {
        return;
      }

      const saved = await strokeApi.createStroke({
        sessionId,
        layerId: 1,
        brushPresetId: 1,
        color: currentLine.color,
        size: currentLine.width,
        opacity: 1,
        points: formattedPoints,
      });

      dispatch(addSavedStroke({ line: currentLine, id: saved.id }));

      sendStroke({
        roomId: ROOM_ID,
        visitorId,
        strokeId: saved.id,
        layerId: 1,
        brushPresetId: 1,
        color: currentLine.color,
        size: currentLine.width,
        opacity: 1,
        points: formattedPoints,
      });

      dispatch(syncPendingStrokes());
    } catch (error) {
      console.error('[DrawingBoard] save stroke failed:', error);
      dispatch(queueStroke(strokePayload));
    } finally {
      dispatch(stopDrawing());
    }
  };

  return (
    <div
      ref={containerRef} 
      className="drawing-board-container"
      style={{
        cursor: isSpacePressed 
          ? (isDragging ? 'grabbing' : 'grab') 
          : 'none'
      }}
    >
      <Stage
        width={dimensions.width}
        height={dimensions.height}
        scaleX={scale}
        scaleY={scale}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        onTouchStart={handleMouseDown}
        onTouchMove={handleMouseMove}
        onTouchEnd={handleMouseUp}
        style={{ backgroundColor: '#f0f2f5' }}
      >
        <Layer x={panOffset.x} y={panOffset.y}>
          {lines.map((line, i) => (
            <KonvaLine
              key={`${i}-${line.points.length}`}
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

          {Object.values(remoteCursors).map((remoteCursor) => (
            <React.Fragment key={remoteCursor.visitorId}>
              <KonvaCircle
                x={remoteCursor.x}
                y={remoteCursor.y}
                radius={6}
                fill={remoteCursor.color}
                opacity={0.8}
                listening={false}
              />
              <KonvaText
                x={remoteCursor.x + 10}
                y={remoteCursor.y + 10}
                text={remoteCursor.visitorId.slice(0, 4)}
                fontSize={12}
                fill={remoteCursor.color}
                listening={false}
              />
            </React.Fragment>
          ))}

          {cursorPos && !isSpacePressed && (
            <KonvaCircle
              x={(cursorPos.x - panOffset.x) / scale}
              y={(cursorPos.y - panOffset.y) / scale}
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