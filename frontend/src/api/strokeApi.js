const API_BASE_URL = 'http://localhost:8080';

export const strokeApi = {
  async getStrokesBySession(sessionId) {
    console.log(`[API] Загружаю штрихи для сессии ${sessionId}...`);
    const res = await fetch(`${API_BASE_URL}/api/strokes/session/${sessionId}`);
    if (!res.ok) {
      console.error(`[API ERROR] Не удалось загрузить штрихи:`, res.status, res.statusText);
      throw new Error('Не удалось загрузить штрихи');
    }
    const data = await res.json();
    console.log(`[API] Получено штрихов:`, data.length, data);
    return data;
  },

  async createStroke({ sessionId, layerId, brushPresetId = 1, color, size, opacity = 1.0, points }) {
    const strokeData = {
      sessionId,
      layerId,
      brushPresetId,
      color,
      size,
      opacity,
      points: JSON.stringify(points),
    };

    console.log(`[API] Отправляю штрих на сохранение:`, strokeData);

    const res = await fetch(`${API_BASE_URL}/api/strokes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(strokeData),
    });

    if (!res.ok) {
      console.error(`[API ERROR] Ошибка сохранения штриха:`, res.status, res.statusText);
      const errorText = await res.text();
      console.error(`[API ERROR BODY]`, errorText);
      throw new Error('Не удалось сохранить штрих');
    }

    const savedStroke = await res.json();
    console.log(`[API] Штрих успешно сохранён:`, savedStroke);
    return savedStroke;
  },

  async deleteStroke(strokeId) {
    console.log(`[API] Удаляю штрих с id=${strokeId}`);
    const res = await fetch(`${API_BASE_URL}/api/strokes/${strokeId}`, {
      method: 'DELETE',
    });
    if (!res.ok) {
      console.error(`[API ERROR] Ошибка удаления штриха:`, res.status, res.statusText);
      throw new Error('Не удалось удалить штрих');
    }
    console.log(`[API] Штрих ${strokeId} удалён`);
    return true;
  },
};