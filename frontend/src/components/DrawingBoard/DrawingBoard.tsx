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
  clearCanvas,
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
  const linesRef = useRef(lines);
  const lastCursorSentAtRef = useRef<number>(0);
  const lastPanPointRef = useRef<{ x: number; y: number } | null>(null);
  const loadedSessionRef = useRef<number | null>(null);
  const warnedAboutMissingSessionRef = useRef(false);
  const localStrokeIdsRef = useRef<Set<number>>(new Set());

  const [viewportSize, setViewportSize] = useState({ width: 1200, height: 800 });
  const [cursorPos, setCursorPos] = useState<{ x: number; y: number } | null>(null);
  const [remoteCursors, setRemoteCursors] = useState<Record<string, RemoteCursor>>({});
  const [scale, setScale] = useState(1);
  const [panOffset, setPanOffset] = useState({ x: 0, y: 0 });
  const [isPanning, setIsPanning] = useState(false);

  useEffect(() => {
    linesRef.current = lines;
  }, [lines]);

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

  const rebuildLines = (nextLines: typeof lines) => {
    const baseId = Date.now();

    dispatch(clearCanvas());

    nextLines.forEach((line, index) => {
      dispatch(
        addSavedStroke({
          id: baseId + index,
          line,
        })
      );
    });
  };

  useEffect(() => {
    loadedSessionRef.current = null;
    warnedAboutMissingSessionRef.current = false;
    localStrokeIdsRef.current.clear();
    setRemoteCursors({});
    setCursorPos(null);
  }, [boardId]);

  useEffect(() => {
    if (board?.id && visitorId) {
      dispatch(loadHistoryFromDB({ boardId: board.id, visitorId }));
    }
  }, [board?.id, visitorId, dispatch]);

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

          if (savedStroke.id) {
            localStrokeIdsRef.current.add(savedStroke.id);
          }

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
    const disconnect = connectDrawingSocket({
      roomId: boardId,
      visitorId,
      visitorName,
      color: visitorColor,
      onEvent: (event: DrawingSocketEvent) => {
        if (event.type === 'stroke_draw') {
          const rawStrokeType =
            (event as any).strokeType ??
            (event as any).lineType ??
            (event as any).toolType ??
            (event as any).controlType;

          if (rawStrokeType === 'clear') {
            if (event.visitorId === visitorId) return;

            dispatch(stopDrawing());
            dispatch(clearCanvas());
            localStrokeIdsRef.current.clear();
            setCursorPos(null);

            dispatch(
              addHistoryEvent({
                id: `socket-clear-${Date.now()}-${Math.random()}`,
                actionText: 'Другой пользователь очистил холст',
                time: new Date().toLocaleTimeString(),
              })
            );

            return;
          }

          if (rawStrokeType === 'undo') {
            if (event.visitorId === visitorId) return;

            dispatch(stopDrawing());

            const nextLines = linesRef.current.slice(0, -1);
            rebuildLines(nextLines);

            dispatch(
              addHistoryEvent({
                id: `socket-undo-${Date.now()}-${Math.random()}`,
                actionText: 'Другой пользователь отменил последнее действие',
                time: new Date().toLocaleTimeString(),
              })
            );

            return;
          }

          const incomingStrokeId = Number((event as any).strokeId);

          if (incomingStrokeId && localStrokeIdsRef.current.has(incomingStrokeId)) {
            return;
          }

          const points = Array.isArray((event as any).points) ? (event as any).points : [];
          const konvaPoints = points.flatMap((p: any) => [Number(p.x), Number(p.y)]);

          if (konvaPoints.length < 2) return;

          if (incomingStrokeId) {
            localStrokeIdsRef.current.add(incomingStrokeId);
          }

          const normalizedStrokeType = rawStrokeType === 'eraser' ? 'eraser' : 'brush';
          const strokeColor =
            typeof (event as any).color === 'string' ? (event as any).color : '#000000';

          dispatch(
            addSavedStroke({
              id: incomingStrokeId || Date.now(),
              line: {
                points: konvaPoints,
                color: strokeColor,
                width: Number((event as any).size || 5),
                type: normalizedStrokeType,
              },
            })
          );

          dispatch(
            addHistoryEvent({
              id: `socket-${incomingStrokeId || Date.now()}-${Math.random()}`,
              actionText:
                rawStrokeType === 'eraser'
                  ? 'Другой пользователь стер часть рисунка'
                  : 'Другой пользователь нарисовал штрих',
              time: new Date().toLocaleTimeString(),
            })
          );

          return;
        }

        if (!isInfinite) return;

        if (event.visitorId === visitorId) return;

        if (event.type === 'cursor_update') {
          const x = Number((event as any).x);
          const y = Number((event as any).y);

          if (!event.visitorId || Number.isNaN(x) || Number.isNaN(y)) return;

          setRemoteCursors((prev) => ({
            ...prev,
            [event.visitorId as string]: {
              visitorId: event.visitorId as string,
              x,
              y,
              color: (event as any).color || '#ff3366',
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

    const persistedStrokeColor =
      currentLine.type === 'eraser'
        ? board?.backgroundColor || '#ffffff'
        : currentLine.color;

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
        color: persistedStrokeColor,
        size: currentLine.width,
        opacity: 1,
        points: formattedPoints,
        type: currentLine.type,
      } as any);

      if (saved.id) {
        localStrokeIdsRef.current.add(saved.id);
      }

      dispatch(addSavedStroke({ line: currentLine, id: saved.id }));

      try {
        if (board?.id) {
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

      sendStroke({
        roomId: boardId,
        visitorId,
        strokeId: saved.id,
        layerId: 1,
        brushPresetId: 1,
        color: persistedStrokeColor,
        size: currentLine.width,
        opacity: 1,
        points: formattedPoints,
        strokeType: currentLine.type,
        lineType: currentLine.type,
        toolType: currentLine.type,
      } as any);

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
        backgroundColor: board?.backgroundColor || '#ffffff',
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