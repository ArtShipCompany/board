import { Stroke, Point } from '../types/drawing';

const API_BASE_URL = 'http://localhost:8080';

export type CreateStrokeInput = Omit<Stroke, 'id' | 'points'> & {
  points: Point[];
  opacity?: number;
  brushPresetId?: number;
};

export const strokeApi = {
  async getStrokesBySession(sessionId: number): Promise<Stroke[]> {
    const res = await fetch(`${API_BASE_URL}/api/strokes/session/${sessionId}`);
    if (!res.ok) throw new Error('Не удалось загрузить штрихи');
    return res.json();
  },

  async createStroke({sessionId, layerId, brushPresetId = 1, color, size, opacity = 1.0, points}: CreateStrokeInput): Promise<Stroke> {
    const res = await fetch(`${API_BASE_URL}/api/strokes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        sessionId,
        layerId,
        brushPresetId,
        color,
        size,
        opacity,
        points: JSON.stringify(points),
      }),
    });
    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(`Ошибка сохранения штриха: ${errorText}`);
    }
    return res.json();
  },

  async deleteStroke(strokeId: number): Promise<boolean> {
    const res = await fetch(`${API_BASE_URL}/api/strokes/${strokeId}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Не удалось удалить штрих');
    return true;
  },
};