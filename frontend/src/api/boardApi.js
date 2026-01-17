const API_BASE_URL = 'http://localhost:8080';
const BOARD_ID = 1;


export const boardApi = {
  async getMyBoard() {
    const response = await fetch(`${API_BASE_URL}/api/boards/${BOARD_ID}`);
    if (response.status === 404) {
      return this.createEmptyBoard();
    }
    if (!response.ok) throw new Error('Ошибка загрузки доски');
    return response.json();
  },


  async createEmptyBoard() {
    return this.createBoard({
      description: 'Моя доска',
      width: 800,
      height: 600,
      backgroundColor: '#ffffff',
    });
  },


  async createBoard(boardData) {
    const response = await fetch(`${API_BASE_URL}/api/boards`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(boardData),
    });
    if (!response.ok) throw new Error('Ошибка создания доски');
    return response.json();
  },


  async updateBoard(id, boardData) {
    const response = await fetch(`${API_BASE_URL}/api/boards/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(boardData),
    });
    if (!response.ok) throw new Error('Ошибка обновления доски');
    return response.json();
  },
};
