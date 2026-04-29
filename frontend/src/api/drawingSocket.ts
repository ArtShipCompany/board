import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8081/ws';

export type WsEventType =
  | 'visitor_joined'
  | 'visitor_left'
  | 'room_full'
  | 'stroke_draw'
  | 'cursor_update'
  | 'action_execute';

export type DrawingSocketEvent = {
  type: WsEventType | string;
  roomId?: string;
  visitorId?: string;
  visitorName?: string;
  color?: string;
  visitors?: any[];
  points?: { x: number; y: number }[];
  strokeId?: number;
  layerId?: number;
  brushPresetId?: number;
  size?: number;
  opacity?: number;
};

let client: Client | null = null;

export function connectDrawingSocket(params: {
  roomId: string;
  visitorId: string;
  visitorName: string;
  color: string;
  onEvent: (event: DrawingSocketEvent) => void;
  onConnected?: () => void;
  onDisconnected?: () => void;
}) {
  const { roomId, visitorId, visitorName, color, onEvent, onConnected, onDisconnected } = params;

  client = new Client({
    webSocketFactory: () => new SockJS(WS_URL),
    reconnectDelay: 3000,
    debug: (msg) => {
      console.log('[WS]', msg);
    },
    onConnect: () => {
      console.log('[WS] connected');

      client?.subscribe(`/topic/room/${roomId}`, (message: IMessage) => {
        try {
          const event = JSON.parse(message.body);
          console.log('[WS] received:', event);
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
      console.log('[WS] disconnected');
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
  roomId: string;
  visitorId: string;
  visitorName: string;
  color: string;
}) {
  publish(`/app/room/${payload.roomId}/join`, payload);
}

export function sendLeaveRoom(payload: {
  roomId: string;
  visitorId: string;
}) {
  publish(`/app/room/${payload.roomId}/leave`, payload);
}

export function sendStroke(payload: {
  roomId: string;
  visitorId: string;
  strokeId?: number;
  layerId: number;
  brushPresetId: number;
  color: string;
  size: number;
  opacity: number;
  points: { x: number; y: number }[];
}) {
  publish(`/app/room/${payload.roomId}/stroke`, {
    type: 'stroke_draw',
    ...payload,
  });
}

export function sendCursor(payload: {
  roomId: string;
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

function publish(destination: string, body: unknown) {
  if (!client || !client.connected) {
    console.warn('[WS] not connected, skip publish:', destination, body);
    return;
  }

  client.publish({
    destination,
    body: JSON.stringify(body),
  });
}