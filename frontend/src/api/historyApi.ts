import { ActionHistoryRecord } from '../types/drawing';

const API_BASE_URL = `/api`;

export const historyApi = {
  async getBoardHistory(boardId: number): Promise<ActionHistoryRecord[]> {
    const res = await fetch(`${API_BASE_URL}/action-history/board/${boardId}`);

    if (!res.ok) {
      throw new Error('Не удалось загрузить историю действий');
    }
    return await res.json();
  },

  async createAction(record: {
    boardId: number;
    userId?: number;
    actionType: 'DRAW' | 'ERASE' | 'CLEAR' | 'COLOR_CHANGE' | 'TOOL_CHANGE';
    targetType?: string;
    targetId?: number;
    details?: string;
    previousData?: string | null;
    sessionId?: number | null;
  }): Promise<void> {
    const res = await fetch(`${API_BASE_URL}/action-history`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        boardId: record.boardId,
        userId: record.userId || 1,
        actionType: record.actionType,
        targetType: record.targetType || 'BOARD',
        targetId: record.targetId || record.boardId,
        newData: record.details || null,
        previousData: record.previousData || null,
        sessionId: record.sessionId || null,
      }),
    });

    if (!res.ok) {
      console.error('[historyApi] Failed to save action:', await res.text());
    }
  },
};