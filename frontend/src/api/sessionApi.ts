import { DrawingSession } from '../types/drawing';

const HOST = window.location.hostname;
const API_BASE_URL = `http://${HOST}:8081/api`;

export type CreateSessionInput = Pick<DrawingSession, 'boardId' | 'userId' | 'layerId'> & {
  brushPresetId?: number;
};

export const sessionApi = {
  async getActiveSessionByBoard(boardId: number): Promise<DrawingSession | null> {
    const res = await fetch(`${API_BASE_URL}/drawing-sessions/board/${boardId}`);
    if (!res.ok) throw new Error('Ошибка загрузки сессий');
    const sessions = await res.json();
    return sessions.length > 0 ? sessions[0] : null;
  },

  async createSession({ boardId, userId, layerId, brushPresetId = 1 }: CreateSessionInput): Promise<DrawingSession> {
    const res = await fetch(`${API_BASE_URL}/drawing-sessions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        boardId,
        userId,
        brushPresetId,
        layerId,
      }),
    });
    if (!res.ok) throw new Error('Не удалось создать сессию рисования');
    return res.json();
  },
};