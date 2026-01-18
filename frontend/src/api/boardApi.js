const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const BOARD_ID = 1;

export const boardApi = {
  async getMyBoard() {
    const response = await fetch(`${API_BASE_URL}/boards/${BOARD_ID}`, {
      headers: {
        'Accept': 'application/json'
      }
    });

    if (response.status === 404) {
      console.log('Board not found, creating new one');
      return this.createEmptyBoard();
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

  async createBoard(boardData) {
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

  async updateBoard(id, boardData) {
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