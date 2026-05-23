import { Stroke, Point } from '../types/drawing';

const API_BASE_URL = '/api';

export type CreateStrokeInput = Omit<Stroke, 'id' | 'points'> & {
  points: Point[];
  opacity?: number;
  brushPresetId?: number;
};

function safeParsePoints(rawPoints: unknown): Point[] {
  try {
    if (!rawPoints) return [];

    if (typeof rawPoints === 'string') {
      const parsed = JSON.parse(rawPoints || '[]');
      return safeParsePoints(parsed);
    }

    if (Array.isArray(rawPoints)) {
      if (rawPoints.length === 0) return [];

      // если массив уже в виде [{x, y}, ...]
      if (typeof rawPoints[0] === 'object' && rawPoints[0] !== null) {
        return rawPoints
          .map((point: any) => ({
            x: Number(point?.x),
            y: Number(point?.y),
          }))
          .filter(
            (point) => !Number.isNaN(point.x) && !Number.isNaN(point.y)
          );
      }

      if (typeof rawPoints[0] === 'number') {
        const numericPoints = rawPoints as number[];
        const result: Point[] = [];

        for (let i = 0; i < numericPoints.length; i += 2) {
          const x = Number(numericPoints[i]);
          const y = Number(numericPoints[i + 1]);

          if (!Number.isNaN(x) && !Number.isNaN(y)) {
            result.push({ x, y });
          }
        }

        return result;
      }
    }

    return [];
  } catch (error) {
    console.warn('[strokeApi] Не удалось распарсить points:', rawPoints, error);
    return [];
  }
}

function normalizeStroke(stroke: any): Stroke {
  return {
    ...stroke,
    id: Number(stroke.id),
    sessionId: Number(stroke.sessionId),
    layerId: Number(stroke.layerId),
    brushPresetId: Number(stroke.brushPresetId ?? 1),
    color: stroke.color ?? '#000000',
    size: Number(stroke.size ?? stroke.width ?? 1),
    opacity: Number(stroke.opacity ?? 1),
    points: safeParsePoints(stroke.points),
  };
}

async function safeReadText(res: Response): Promise<string> {
  try {
    return await res.text();
  } catch {
    return '';
  }
}

export const strokeApi = {
  async getStrokesBySession(sessionId: number): Promise<Stroke[]> {
    if (!sessionId || Number.isNaN(Number(sessionId))) {
      console.warn('[strokeApi] getStrokesBySession: некорректный sessionId:', sessionId);
      return [];
    }

    const res = await fetch(`${API_BASE_URL}/strokes/session/${sessionId}`, {
      headers: {
        Accept: 'application/json',
      },
    });

    if (res.status === 404) {
      return [];
    }

    if (!res.ok) {
      const errorText = await safeReadText(res);
      console.error('[strokeApi] getStrokesBySession error:', res.status, errorText);
      throw new Error('Не удалось загрузить штрихи');
    }

    const data = await res.json();

    if (!Array.isArray(data)) {
      console.warn('[strokeApi] getStrokesBySession: сервер вернул не массив', data);
      return [];
    }

    return data.map(normalizeStroke);
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
    if (!sessionId || Number.isNaN(Number(sessionId))) {
      console.error('[strokeApi] createStroke: sessionId отсутствует или невалидный:', sessionId);
      throw new Error('Невозможно сохранить штрих: отсутствует sessionId');
    }

    const payload = {
      sessionId: Number(sessionId),
      layerId: Number(layerId),
      brushPresetId: Number(brushPresetId),
      color,
      size: Math.round(Number(size)),
      opacity: Number(opacity),
      points: JSON.stringify(points ?? []),
    };

    console.log('[strokeApi] createStroke payload:', payload);

    const res = await fetch(`${API_BASE_URL}/strokes`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const errorText = await safeReadText(res);
      console.error('[strokeApi] createStroke error:', res.status, errorText);
      throw new Error(`Ошибка сохранения штриха: ${errorText}`);
    }

    const text = await safeReadText(res);

    if (!text) {
      return normalizeStroke({
        id: Date.now(),
        ...payload,
      });
    }

    try {
      const saved = JSON.parse(text);
      return normalizeStroke(saved);
    } catch (error) {
      console.warn('[strokeApi] createStroke: ответ не JSON, использую локальную нормализацию', error);
      return normalizeStroke({
        id: Date.now(),
        ...payload,
      });
    }
  },

  async deleteStroke(strokeId: number): Promise<boolean> {
    const res = await fetch(`${API_BASE_URL}/strokes/${strokeId}`, {
      method: 'DELETE',
    });

    if (!res.ok) {
      const errorText = await safeReadText(res);
      console.error('[strokeApi] deleteStroke error:', res.status, errorText);
      throw new Error('Не удалось удалить штрих');
    }

    return true;
  },

  async clearSessionStrokes(sessionId: number): Promise<boolean> {
    const res = await fetch(`${API_BASE_URL}/strokes/session/${sessionId}`, {
      method: 'DELETE',
    });

    if (!res.ok) {
      const errorText = await safeReadText(res);
      console.error('[strokeApi] clearSessionStrokes error:', res.status, errorText);
      throw new Error('Не удалось очистить холст на сервере');
    }

    return true;
  },
};