import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type WsEventType =
  | 'visitor_joined'
  | 'visitor_left'
  | 'room_full'
  | 'stroke_draw'
  | 'cursor_update'
  | 'action_execute';

export type BoardActionType = 'undo' | 'clear';

export type DrawingSocketEvent = {
  type: WsEventType | string;

  roomId?: number;
  visitorId?: string;
  visitorName?: string;
  color?: string;
  visitors?: unknown[];

  strokeId?: number;
  layerId?: number;
  brushPresetId?: number;
  size?: number;
  opacity?: number;
  points?: { x: number; y: number }[];

  strokeType?: 'brush' | 'eraser';
  action?: BoardActionType;
  actionId?: string;
  sessionId?: number;

  x?: number;
  y?: number;
};

export type SendStrokePayload = {
  roomId: number;
  visitorId: string;
  strokeId?: number;
  layerId: number;
  brushPresetId: number;
  color: string;
  size: number;
  opacity: number;
  points: { x: number; y: number }[];
  strokeType?: 'brush' | 'eraser';
};

export type SendBoardActionPayload = {
  roomId: number;
  visitorId: string;
  action: BoardActionType;
  actionId: string;
  sessionId?: number;
  strokeId?: number;
};

let client: Client | null = null;

export function connectDrawingSocket(params: {
  roomId: number;
  visitorId: string;
  visitorName: string;
  color: string;
  onEvent: (event: DrawingSocketEvent) => void;
  onConnected?: () => void;
  onDisconnected?: () => void;
}) {
  const { roomId, visitorId, visitorName, color, onEvent, onConnected, onDisconnected } = params;

  client = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 3000,
    onConnect: () => {
      console.log('[WS] connected');

      client?.subscribe(`/topic/room/${roomId}`, (message: IMessage) => {
        try {
          const event = JSON.parse(message.body) as DrawingSocketEvent;
          console.log('[WS EVENT]', event);
          onEvent(event);
        } catch (error) {
          console.error('[WS] failed to parse message:', message.body, error);
        }
      });

      sendJoinRoom({
        roomId,
        visitorId,
        visitorName,
        color,
      });

      onConnected?.();
    },
    onDisconnect: () => {
      onDisconnected?.();
    },
    onStompError: (frame) => {
      console.error('[WS] stomp error:', frame.headers.message, frame.body);
    },
    onWebSocketError: (error) => {
      console.error('[WS] websocket error:', error);
    },
  });

  client.activate();

  return () => {
    sendLeaveRoom({ roomId, visitorId });
    client?.deactivate();
    client = null;
  };
}

export function sendJoinRoom(payload: {
  roomId: number;
  visitorId: string;
  visitorName: string;
  color: string;
}) {
  publish(`/app/room/${payload.roomId}/join`, {
    type: 'visitor_joined',
    ...payload,
  });
}

export function sendLeaveRoom(payload: {
  roomId: number;
  visitorId: string;
}) {
  publish(`/app/room/${payload.roomId}/leave`, {
    type: 'visitor_left',
    ...payload,
  });
}

export function sendStroke(payload: SendStrokePayload) {
  publish(`/app/room/${payload.roomId}/stroke`, {
    type: 'stroke_draw',
    ...payload,
  });
}

export function sendBoardAction(payload: SendBoardActionPayload) {
  publish(`/app/room/${payload.roomId}/action`, {
    type: 'action_execute',
    ...payload,
  });
}

export function sendCursor(payload: {
  roomId: number;
  visitorId: string;
  x: number;
  y: number;
  color: string;
}) {
  publish(`/app/room/${payload.roomId}/cursor`, {
    type: 'cursor_update',
    ...payload,
  });
}

function publish(destination: string, body: Record<string, unknown>) {
  if (!client || !client.connected) {
    console.warn('[WS] not connected, skip publish:', destination, body);
    return;
  }

  client.publish({
    destination,
    body: JSON.stringify(body),
  });
}