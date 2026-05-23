import React, { useState, useRef, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import { setTool, setColor, setBrushSize, undoLastStroke, clearCanvas } from '../../store/drawingSlice';
import { logAction } from '../../store/historySlice';
import { historyApi } from '../../api/historyApi';
import './Toolbar.css';

const Toolbar: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  
  const { tool, color, brushSize, lines, currentLine, board } = useSelector((state: RootState) => state.drawing);
  
  const [showColorPicker, setShowColorPicker] = useState<boolean>(false);
  const colorPickerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (colorPickerRef.current && !colorPickerRef.current.contains(event.target as Node)) {
        setShowColorPicker(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleColorChange = async (newColor: string) => {
    dispatch(setColor(newColor));
    if (tool === 'eraser') {
      dispatch(setTool('brush'));
    }
    if (board?.id) {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'COLOR_CHANGE',
        details: `Цвет: ${newColor}`,
      });
    }
    dispatch(logAction(`Выбрал цвет: ${newColor.toUpperCase()}`));
  };

  const handleBrushSizeChange = (size: number) => {
    dispatch(setBrushSize(size));
  };

  const handleBrushSizeLog = async () => {
    if (board?.id) {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'TOOL_CHANGE',
        details: `Размер кисти: ${brushSize}`,
      });
    }
    dispatch(logAction(`Изменил размер кисти на ${brushSize}`));
  };

  const handleToolChange = async (newTool: 'brush' | 'eraser') => {
    dispatch(setTool(newTool));
    const toolName = newTool === 'brush' ? 'кисть' : 'ластик';
    if (board?.id) {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'TOOL_CHANGE',
        details: `Инструмент: ${toolName}`,
      });
    }
    dispatch(logAction(`Взял ${toolName}`));
  };

  const handleClear = async () => {
    if (window.confirm('Вы уверены, что хотите очистить холст?')) {
      dispatch(clearCanvas());
      if (board?.id) {
        await historyApi.createAction({
          boardId: board.id,
          actionType: 'CLEAR',
          details: 'Холст очищен',
        });
      }
      dispatch(logAction('Очистил холст'));
    }
  };

  const handleUndo = async () => {
    dispatch(undoLastStroke());
    if (board?.id) {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'TOOL_CHANGE',
        details: 'Отмена действия',
      });
    }
    dispatch(logAction('Отменил действие (Назад)'));
  };

  const toggleColorPicker = (e: React.MouseEvent) => {
    e.stopPropagation();
    setShowColorPicker(!showColorPicker);
  };

  const defaultColors = [
    '#000000', '#FF0000', '#00FF00', '#0000FF',
    '#FFFF00', '#FF00FF', '#00FFFF', '#FFA500',
    '#800080', '#FFFFFF', '#808080', '#A52A2A'
  ];

  return (
    <div className="toolbar">
      <div className="toolbar-section">
        <h3>Инструменты</h3>
        <div className="tool-buttons">
          <button
            className={`tool-button ${tool === 'brush' ? 'active' : ''}`}
            onClick={() => handleToolChange('brush')}
          >
            <span className="tool-icon">🖌️</span> Кисть
          </button>
          <button
            className={`tool-button ${tool === 'eraser' ? 'active' : ''}`}
            onClick={() => handleToolChange('eraser')}
          >
            <span className="tool-icon">🧹</span> Ластик
          </button>
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Цвет</h3>
        <div className="color-picker-section" ref={colorPickerRef}>
          <div className="color-display" onClick={toggleColorPicker}>
            <div className="current-color-display" style={{ backgroundColor: color }} />
            <span className="color-hex">{color.toUpperCase()}</span>
            <span className="color-arrow">{showColorPicker ? '▲' : '▼'}</span>
          </div>
          {showColorPicker && (
            <div className="color-picker-modal">
              <input
                type="color"
                value={color}
                onChange={(e) => handleColorChange(e.target.value)}
                className="color-wheel-input"
              />
              <div className="default-colors-grid">
                {defaultColors.map((c) => (
                  <button
                    key={c}
                    className={`default-color-button ${color === c ? 'active' : ''}`}
                    style={{ backgroundColor: c }}
                    onClick={() => handleColorChange(c)}
                  />
                ))}
              </div>
              <button className="close-picker-button" onClick={() => setShowColorPicker(false)}>
                Закрыть
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Размер</h3>
        <div className="brush-size-control">
          <input
            type="range"
            min="1" max="50"
            value={brushSize}
            onChange={(e) => handleBrushSizeChange(parseInt(e.target.value))}
            onMouseUp={handleBrushSizeLog}
            onTouchEnd={handleBrushSizeLog}
            className="brush-size-slider"
          />
          <div className="brush-size-preview">
            <div
              className="brush-preview-circle"
              style={{
                width: `${brushSize}px`,
                height: `${brushSize}px`,
                backgroundColor: tool === 'eraser' ? '#ffffff' : color,
                border: tool === 'eraser' ? '1px solid #cbd5e0' : 'none'
              }}
            />
          </div>
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Действия</h3>
        <div className="action-buttons">
          <button 
            className="action-button undo" 
            onClick={handleUndo}
            disabled={lines.length === 0}
          >
            Назад
          </button>
          <button 
            className="action-button clear" 
            onClick={handleClear} 
            disabled={lines.length === 0 && !currentLine}
          >
            Очистить
          </button>
        </div>
      </div>
    </div>
  );
};

export default Toolbar;