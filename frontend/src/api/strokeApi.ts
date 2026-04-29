import { Stroke, Point } from '../types/drawing';

const API_BASE_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');

export type CreateStrokeInput = Omit<Stroke, 'id' | 'points'> & {
  points: Point[];
  opacity?: number;
  brushPresetId?: number;
};

function normalizeStroke(stroke: any): Stroke {
  return {
    ...stroke,
    points:
      typeof stroke.points === 'string'
        ? JSON.parse(stroke.points || '[]')
        : stroke.points,
  };
}

export const strokeApi = {
  async getStrokesBySession(sessionId: number): Promise<Stroke[]> {
    const res = await fetch(`${API_BASE_URL}/strokes/session/${sessionId}`);

    if (!res.ok) {
      const errorText = await res.text();
      console.error('[strokeApi] getStrokesBySession error:', res.status, errorText);
      throw new Error('Не удалось загрузить штрихи');
    }

    const strokes = await res.json();
    return strokes.map(normalizeStroke);
  },

  async createStroke({
    sessionId,
    layerId,
    brushPresetId = 1,
    color,
    size,
    opacity = 1.0,
    points,
  }: CreateStrokeInput): Promise<Stroke> {
    const payload = {
      sessionId: Number(sessionId),
      layerId: Number(layerId),
      brushPresetId: Number(brushPresetId),
      color,
      size: Math.round(Number(size)),
      opacity: Number(opacity),
      points: JSON.stringify(points),
    };

    console.log('[strokeApi] createStroke payload:', payload);

    const res = await fetch(`${API_BASE_URL}/strokes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error('[strokeApi] createStroke error:', res.status, errorText);
      throw new Error(`Ошибка сохранения штриха: ${errorText}`);
    }

    const saved = await res.json();
    return normalizeStroke(saved);
  },

  async deleteStroke(strokeId: number): Promise<boolean> {
    const res = await fetch(`${API_BASE_URL}/strokes/${strokeId}`, {
      method: 'DELETE',
    });

    if (!res.ok) {
      const errorText = await res.text();
      console.error('[strokeApi] deleteStroke error:', res.status, errorText);
      throw new Error('Не удалось удалить штрих');
    }

    return true;
  },
};