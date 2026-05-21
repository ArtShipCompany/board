export interface Point {
  x: number;
  y: number;
}

export interface Line {
  points: Point[];
  color: string;
  width: number;
  type: 'brush' | 'eraser';
}

export interface Board {
  id: number;
  title: string;
  description?: string;
  width: number;
  height: number;
  backgroundColor: string;
}

export interface DrawingSession {
  id: number;
  boardId: number;
  userId: number;
  layerId: number;
}

export interface Stroke {
  id: number;
  sessionId: number;
  layerId: number;
  color: string;
  size: number;
  points: string;
}

export interface ActionHistoryRecord {
  id: number;
  boardId: number;
  userId: number;
  actionType: string;
  targetType: string;
  targetId: number;
  previousData: string | null;
  newData: string | null;
  timestamp: string;
  sessionId: number | null;
}