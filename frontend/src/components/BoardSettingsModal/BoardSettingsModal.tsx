import React, { useState } from 'react';
import { Board } from '../../types/drawing';
import './BoardSettingsModal.css';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  board: Board;
  onSave: (data: Partial<Board>) => void;
}

export const BoardSettingsModal: React.FC<Props> = ({ isOpen, onClose, board, onSave }) => {
  const [formData, setFormData] = useState<Board>(board);
  const [orientation, setOrientation] = useState<'landscape' | 'portrait'>(
    board.width >= board.height ? 'landscape' : 'portrait'
  );

  if (!isOpen) return null;

  const handleSubmit = () => {
    onSave(formData);
    onClose();
  };

  const handleOrientationChange = (o: 'landscape' | 'portrait') => {
    setOrientation(o);
    setFormData(prev => ({ ...prev, width: prev.height, height: prev.width }));
  };

  // Функция для тестирования бесконечной доски
  const handleTestInfiniteBoard = () => {
    // Открываем новую вкладку. 
    // Замените '/infinite-board' на реальный маршрут (URL) вашей страницы тестирования
    window.open('/infinite-board', '_blank');
    
    // Если нужно закрыть модалку после клика, раскомментируйте строку ниже:
    // onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3>Настройки доски</h3>
        
        <div className="form-group">
          <label>Название</label>
          <input 
            value={formData.title} 
            onChange={e => setFormData({...formData, title: e.target.value})} 
          />
        </div>
        
        <div className="form-row">
          <div className="form-group">
            <label>Ширина</label>
            <input 
              type="number" 
              value={formData.width} 
              onChange={e => setFormData({...formData, width: Number(e.target.value)})} 
            />
          </div>
          <div className="form-group">
            <label>Высота</label>
            <input 
              type="number" 
              value={formData.height} 
              onChange={e => setFormData({...formData, height: Number(e.target.value)})} 
            />
          </div>
        </div>
        
        <div className="form-group">
            <label>Ориентация</label>
            <select 
              value={orientation} 
              onChange={(e) => handleOrientationChange(e.target.value as any)}
            >
                <option value="landscape">Альбомная</option>
                <option value="portrait">Книжная</option>
            </select>
        </div>
        
        <div className="form-group">
          <label>Цвет фона</label>
          <input 
            type="color" 
            value={formData.backgroundColor} 
            onChange={e => setFormData({...formData, backgroundColor: e.target.value})} 
          />
        </div>
        
        {/* Блок с кнопками */}
        <div className="modal-actions">
          {/* Новая кнопка для теста */}
          <button 
            type="button" 
            className="test-btn" 
            onClick={handleTestInfiniteBoard}
            style={{ marginRight: 'auto', backgroundColor: '#4CAF50', color: 'white' }} // Базовые стили для выделения (лучше перенести в .css)
          >
            Тест бесконечной доски
          </button>
          
          <button type="button" onClick={onClose}>Отмена</button>
          <button type="button" onClick={handleSubmit}>Применить</button>
        </div>
      </div>
    </div>
  );
};