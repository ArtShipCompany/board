import { Board } from '../types/drawing';

const API_BASE_URL = `/api`;

export const boardApi = {
  async getBoardById(boardId: number) {
    const response = await fetch(`${API_BASE_URL}/boards/${boardId}`, {
      headers: { 'Accept': 'application/json' }
    });

    if (response.status === 404) {
      return null;
    }

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Error loading board:', errorText);
      throw new Error('Ошибка загрузки доски');
    }

    return response.json();
  },

  async createEmptyBoard() {
    return this.createBoard({
      title: 'Моя доска',
      description: 'Моя доска',
      width: 800,
      height: 600,
      backgroundColor: '#ffffff',
    });
  },

  async createBoard(boardData: Partial<Board>): Promise<Board> {
    console.log('Creating board with data:', boardData);

    const response = await fetch(`${API_BASE_URL}/boards`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(boardData),
    });

    console.log('Create response status:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Error creating board:', errorText);
      throw new Error('Ошибка создания доски');
    }

    return response.json();
  },

  async updateBoard(id: number, boardData: Partial<Board>): Promise<Board> {
    const response = await fetch(`${API_BASE_URL}/boards/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(boardData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Error updating board:', errorText);
      throw new Error('Ошибка обновления доски');
    }

    return response.json();
  },
};