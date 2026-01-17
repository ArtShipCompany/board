const API_BASE_URL = 'http://localhost:8080';

export const sessionApi = {
  async getActiveSessionByBoard(boardId) {
    const res = await fetch(`${API_BASE_URL}/api/drawing-sessions/board/${boardId}`);
    if (!res.ok) throw new Error('Ошибка загрузки сессий');
    const sessions = await res.json();
    return sessions.length > 0 ? sessions[0] : null;
  },

  async createSession({ boardId, userId, layerId, brushPresetId = 1 }) {
    const res = await fetch(`${API_BASE_URL}/api/drawing-sessions`, {
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