import React, { useEffect, useMemo, useRef, useState } from 'react';
import {
  Stage,
  Layer,
  Line as KonvaLine,
  Circle as KonvaCircle,
  Path,
  Rect,
} from 'react-konva';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import {
  startDrawing,
  draw,
  stopDrawing,
  addSavedStroke,
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
import { historyApi } from '../../api/historyApi';
import { loadHistoryFromDB, addHistoryEvent } from '../../store/historySlice';
import { Minimap } from '../Minimap/Minimap';
import './DrawingBoard.css';

type RemoteCursor = {
  visitorId: string;
  x: number;
  y: number;
  color: string;
};

type BoardMode = 'solo' | 'infinite';

interface DrawingBoardProps {
  boardId: number;
  mode: BoardMode;
}

type StrokePoint = {
  x: number;
  y: number;
};

function createClientId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }
  return `visitor-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function linePointsToPointObjects(points: number[]) {
  const result: StrokePoint[] = [];
  for (let i = 0; i < points.length; i += 2) {
    result.push({ x: points[i], y: points[i + 1] });
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

function normalizePointObjects(rawPoints: unknown): StrokePoint[] {
  if (!rawPoints) return [];

  let parsed = rawPoints;

  if (typeof rawPoints === 'string') {
    try {
      parsed = JSON.parse(rawPoints);
    } catch {
      return [];
    }
  }

  if (!Array.isArray(parsed)) return [];

  if (parsed.length === 0) return [];

  if (typeof parsed[0] === 'number') {
    const numeric = parsed as number[];
    const result: StrokePoint[] = [];
    for (let i = 0; i < numeric.length; i += 2) {
      result.push({
        x: Number(numeric[i]),
        y: Number(numeric[i + 1]),
      });
    }
    return result;
  }

  return (parsed as any[])
    .map((point) => ({
      x: Number(point?.x),
      y: Number(point?.y),
    }))
    .filter((point) => !Number.isNaN(point.x) && !Number.isNaN(point.y));
}

function normalizeKonvaPoints(rawPoints: unknown): number[] {
  return normalizePointObjects(rawPoints).flatMap((point) => [point.x, point.y]);
}

export const DrawingBoard: React.FC<DrawingBoardProps> = ({ boardId, mode }) => {
  const dispatch = useDispatch<AppDispatch>();

  const { lines, currentLine, sessionId, brushSize, tool, color, board } = useSelector(
    (state: RootState) => state.drawing
  );

  const isOnline = useNetworkStatus();
  const isInfinite = mode === 'infinite';

  const containerRef = useRef<HTMLDivElement>(null);
  const lastCursorSentAtRef = useRef<number>(0);
  const lastPanPointRef = useRef<{ x: number; y: number } | null>(null);
  const loadedSessionRef = useRef<number | null>(null);
  const warnedAboutMissingSessionRef = useRef(false);

  const [viewportSize, setViewportSize] = useState({ width: 1200, height: 800 });
  const [cursorPos, setCursorPos] = useState<{ x: number; y: number } | null>(null);
  const [remoteCursors, setRemoteCursors] = useState<Record<string, RemoteCursor>>({});
  const [scale, setScale] = useState(1);
  const [panOffset, setPanOffset] = useState({ x: 0, y: 0 });
  const [isPanning, setIsPanning] = useState(false);

  const visitorId = useMemo(() => {
    const existing = localStorage.getItem('visitorId');
    if (existing) return existing;

    const created = createClientId();
    localStorage.setItem('visitorId', created);
    return created;
  }, []);

  const visitorName = useMemo(() => {
    const existing = localStorage.getItem('visitorName');
    if (existing) return existing;

    const created = `User-${visitorId.slice(0, 4)}`;
    localStorage.setItem('visitorName', created);
    return created;
  }, [visitorId]);

  const visitorColor = useMemo(() => {
    const existing = localStorage.getItem('visitorColor');
    if (existing) return existing;

    const created = makeVisitorColor(visitorId);
    localStorage.setItem('visitorColor', created);
    return created;
  }, [visitorId]);

  const boardWidth = Number(board?.width) || 800;
  const boardHeight = Number(board?.height) || 600;

  useEffect(() => {
    if (!isInfinite && board?.id && visitorId) {
      dispatch(loadHistoryFromDB({ boardId: board.id, visitorId }));
    }
  }, [board?.id, visitorId, dispatch, isInfinite]);

  useEffect(() => {
    if (!isInfinite) return;

    const updateSize = () => {
      if (!containerRef.current) return;
      setViewportSize({
        width: containerRef.current.clientWidth,
        height: containerRef.current.clientHeight,
      });
    };

    updateSize();
    window.addEventListener('resize', updateSize);

    let observer: ResizeObserver | null = null;
    if (typeof ResizeObserver !== 'undefined' && containerRef.current) {
      observer = new ResizeObserver(() => updateSize());
      observer.observe(containerRef.current);
    }

    return () => {
      window.removeEventListener('resize', updateSize);
      observer?.disconnect();
    };
  }, [isInfinite]);

  // Загружаем сохранённые штрихи после перезагрузки страницы
  useEffect(() => {
    if (!sessionId) return;
    if (loadedSessionRef.current === sessionId) return;

    loadedSessionRef.current = sessionId;

    let cancelled = false;

    const loadSavedStrokes = async () => {
      try {
        const savedStrokes = await strokeApi.getStrokesBySession(sessionId);

        if (cancelled) return;

        for (const savedStroke of savedStrokes) {
          const konvaPoints = normalizeKonvaPoints(savedStroke.points);

          if (konvaPoints.length < 2) continue;

          dispatch(
            addSavedStroke({
              id: savedStroke.id || Date.now() + Math.random(),
              line: {
                points: konvaPoints,
                color: savedStroke.color || '#000000',
                width: Number(savedStroke.size ?? 5),
                type: savedStroke.type === 'eraser' ? 'eraser' : 'brush',
              },
            })
          );
        }
      } catch (error) {
        console.warn('[DrawingBoard] Не удалось загрузить сохранённые штрихи:', error);
      }
    };

    loadSavedStrokes();

    return () => {
      cancelled = true;
    };
  }, [sessionId, dispatch]);

  useEffect(() => {
    if (!isInfinite) return () => { };

    const disconnect = connectDrawingSocket({
      roomId: boardId,
      visitorId,
      visitorName,
      color: visitorColor,
      onEvent: (event: DrawingSocketEvent) => {
        if (event.visitorId === visitorId) return;

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
          const x = Number(event.x);
          const y = Number(event.y);

          if (!event.visitorId || Number.isNaN(x) || Number.isNaN(y)) return;

          setRemoteCursors((prev) => ({
            ...prev,
            [event.visitorId as string]: {
              visitorId: event.visitorId as string,
              x,
              y,
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
        }
      },
    });

    return () => disconnect();
  }, [dispatch, visitorId, visitorName, visitorColor, boardId, isInfinite]);

  const toWorldPoint = (point: { x: number; y: number }) => {
    if (!isInfinite) return point;

    return {
      x: (point.x - panOffset.x) / scale,
      y: (point.y - panOffset.y) / scale,
    };
  };

  const handleWheel = (e: any) => {
    if (!isInfinite) return;

    e.evt.preventDefault();

    const stage = e.target.getStage();
    const pointer = stage.getPointerPosition();
    if (!pointer) return;

    const oldScale = scale;
    const scaleBy = 1.1;
    const direction = e.evt.deltaY > 0 ? -1 : 1;
    const newScale = direction > 0 ? oldScale * scaleBy : oldScale / scaleBy;
    const nextScale = Math.max(0.2, Math.min(newScale, 8));

    const worldPoint = {
      x: (pointer.x - panOffset.x) / oldScale,
      y: (pointer.y - panOffset.y) / oldScale,
    };

    setScale(nextScale);
    setPanOffset({
      x: pointer.x - worldPoint.x * nextScale,
      y: pointer.y - worldPoint.y * nextScale,
    });
  };

  const handleMouseDown = (e: any) => {
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();
    if (!pos) return;

    const isMiddleMouse = e.evt.button === 1 || e.evt.buttons === 4;

    if (isInfinite && isMiddleMouse) {
      setIsPanning(true);
      lastPanPointRef.current = pos;
      return;
    }

    const worldPos = toWorldPoint(pos);
    dispatch(startDrawing({ x: worldPos.x, y: worldPos.y }));
  };

  const handleMouseMove = (e: any) => {
    const stage = e.target.getStage();
    const pos = stage.getPointerPosition();
    if (!pos) return;

    if (isInfinite && isPanning && lastPanPointRef.current) {
      const dx = pos.x - lastPanPointRef.current.x;
      const dy = pos.y - lastPanPointRef.current.y;

      setPanOffset((prev) => ({
        x: prev.x + dx,
        y: prev.y + dy,
      }));

      lastPanPointRef.current = pos;
      return;
    }

    const worldPos = toWorldPoint(pos);
    setCursorPos(worldPos);

    const isTouch = e.evt.touches && e.evt.touches.length > 0;
    const isClick = e.evt.buttons === 1;

    if (isClick || isTouch) {
      dispatch(draw({ x: worldPos.x, y: worldPos.y }));
    }

    if (!isInfinite) return;

    const now = Date.now();
    if (now - lastCursorSentAtRef.current > 120) {
      lastCursorSentAtRef.current = now;
      sendCursor({
        roomId: boardId,
        visitorId,
        x: worldPos.x,
        y: worldPos.y,
        color: visitorColor,
      });
    }
  };

  const handleMouseUp = async () => {
    if (isPanning) {
      setIsPanning(false);
      lastPanPointRef.current = null;
      return;
    }

    if (!currentLine) {
      dispatch(stopDrawing());
      return;
    }

    const pointsArray = currentLine.points as unknown as number[];
    const formattedPoints = linePointsToPointObjects(pointsArray);

    if (formattedPoints.length < 2) {
      dispatch(stopDrawing());
      return;
    }

    const tempId = createClientId();
    const strokePayload = { line: currentLine, tempId };

    if (!sessionId) {
      if (!warnedAboutMissingSessionRef.current) {
        console.warn('[DrawingBoard] sessionId отсутствует, штрих временно сохраняется локально');
        warnedAboutMissingSessionRef.current = true;
      }

      dispatch(addSavedStroke({ line: currentLine, id: Date.now() }));
      dispatch(queueStroke(strokePayload));
      dispatch(stopDrawing());
      return;
    }

    try {
      if (!isOnline) {
        dispatch(queueStroke(strokePayload));
        dispatch(addSavedStroke({ line: currentLine, id: Date.now() }));
        dispatch(stopDrawing());
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

      try {
        if (!isInfinite && board?.id) {
          await historyApi.createAction({
            boardId,
            userId: 1,
            actionType: tool === 'eraser' ? 'ERASE' : 'DRAW',
            targetType: 'BOARD',
            targetId: board.id,
            details: `Stroke ID: ${saved.id}|VISITOR:${visitorId}`,
            previousData: null,
            sessionId: null,
          });

          dispatch(
            addHistoryEvent({
              id: saved.id.toString(),
              actionText: tool === 'eraser' ? 'Стер элемент' : 'Нарисовал штрих',
              time: new Date().toLocaleTimeString(),
            })
          );
        }
      } catch (historyError) {
        console.warn('[DrawingBoard] История не сохранилась, но сам штрих сохранён:', historyError);
      }

      if (isInfinite) {
        sendStroke({
          roomId: boardId,
          visitorId,
          strokeId: saved.id,
          layerId: 1,
          brushPresetId: 1,
          color: currentLine.color,
          size: currentLine.width,
          opacity: 1,
          points: formattedPoints,
        });
      }

      dispatch(syncPendingStrokes());
    } catch (error) {
      console.error('[DrawingBoard] save stroke failed:', error);
      dispatch(queueStroke(strokePayload));
      dispatch(addSavedStroke({ line: currentLine, id: Date.now() }));
    } finally {
      dispatch(stopDrawing());
    }
  };

  const stageWidth = isInfinite ? viewportSize.width : boardWidth;
  const stageHeight = isInfinite ? viewportSize.height : boardHeight;

  return (
    <div
      ref={containerRef}
      className="drawing-board-root"
      style={{
        position: 'relative',
        width: isInfinite ? '100%' : boardWidth,
        height: isInfinite ? '100%' : boardHeight,
        backgroundColor: isInfinite
          ? board?.backgroundColor || '#ffffff'
          : board?.backgroundColor || '#ffffff',
        boxShadow: isInfinite ? 'none' : '0 4px 15px rgba(0,0,0,0.1)',
        cursor: isPanning ? 'grabbing' : 'none',
        flexShrink: 0,
        overflow: 'hidden',
      }}
    >
      <Stage
        width={stageWidth}
        height={stageHeight}
        scaleX={isInfinite ? scale : 1}
        scaleY={isInfinite ? scale : 1}
        x={isInfinite ? panOffset.x : 0}
        y={isInfinite ? panOffset.y : 0}
        onWheel={handleWheel}
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        onTouchStart={handleMouseDown}
        onTouchMove={handleMouseMove}
        onTouchEnd={handleMouseUp}
      >
        <Layer>
          {!isInfinite && (
            <Rect
              x={0}
              y={0}
              width={boardWidth}
              height={boardHeight}
              fill={board?.backgroundColor || '#ffffff'}
              listening={false}
            />
          )}

          {lines.map((line, i) => (
            <KonvaLine
              key={`${i}-${line.points.length}`}
              points={line.points as any}
              stroke={line.color}
              strokeWidth={line.width}
              tension={0.5}
              lineCap="round"
              lineJoin="round"
              perfectDrawEnabled={false}
              listening={false}
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
            <Path
              key={remoteCursor.visitorId}
              x={remoteCursor.x}
              y={remoteCursor.y}
              data="M0 0 L0 17 L5 12 L9 19 L12 17 L8 11 L14 11 Z"
              fill={remoteCursor.color}
              stroke="white"
              strokeWidth={1}
              opacity={0.9}
              listening={false}
              scaleX={isInfinite ? 1 / scale : 1}
              scaleY={isInfinite ? 1 / scale : 1}
            />
          ))}

          {cursorPos && !isPanning && (
            <KonvaCircle
              x={cursorPos.x}
              y={cursorPos.y}
              radius={brushSize / 2}
              fill={tool === 'eraser' ? 'transparent' : color}
              stroke={tool === 'eraser' ? '#000000' : color}
              strokeWidth={tool === 'eraser' ? 2 : 0}
              opacity={0.45}
              dash={tool === 'eraser' ? [4, 4] : undefined}
              listening={false}
            />
          )}
        </Layer>
      </Stage>

      {isInfinite && (
        <div
          style={{
            position: 'absolute',
            top: 20,
            right: 20,
            zIndex: 25,
            backgroundColor: 'rgba(255,255,255,0.96)',
            borderRadius: '12px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
            padding: '8px',
          }}
        >
          <Minimap
            lines={lines}
            viewportDimensions={viewportSize}
            scale={scale}
            panOffset={panOffset}
            setScale={setScale}
            setPanOffset={setPanOffset}
            boardBackgroundColor={board?.backgroundColor || '#ffffff'}
          />
        </div>
      )}
    </div>
  );
};

export default DrawingBoard;