import React, { useState, useRef, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../../store';
import {
  setTool,
  setColor,
  setBrushSize,
  clearCanvas,
  addSavedStroke,
} from '../../store/drawingSlice';
import { logAction } from '../../store/historySlice';
import { historyApi } from '../../api/historyApi';
import { sendStroke } from '../../api/drawingSocket';
import './Toolbar.css';

const Toolbar: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();

  const { tool, color, brushSize, lines, currentLine, board } = useSelector(
    (state: RootState) => state.drawing
  );

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

  const getVisitorId = () => {
    let currentVisitorId = localStorage.getItem('visitorId');

    if (!currentVisitorId) {
      currentVisitorId =
        typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function'
          ? crypto.randomUUID()
          : `visitor-${Date.now()}-${Math.random().toString(16).slice(2)}`;

      localStorage.setItem('visitorId', currentVisitorId);
    }

    return currentVisitorId;
  };

  const rebuildLines = (nextLines: typeof lines) => {
    const baseId = Date.now();

    dispatch(clearCanvas());

    nextLines.forEach((line, index) => {
      dispatch(
        addSavedStroke({
          id: baseId + index,
          line,
        })
      );
    });
  };

  const sendControlAction = (action: 'undo' | 'clear') => {
    if (!board?.id) return;

    const visitorId = getVisitorId();

    sendStroke({
      roomId: board.id,
      visitorId,
      strokeId: Date.now(),
      layerId: 1,
      brushPresetId: 1,
      color: board.backgroundColor || '#ffffff',
      size: 1,
      opacity: 1,
      points: [
        { x: 0, y: 0 },
        { x: 0.001, y: 0.001 },
      ],
      strokeType: action,
      lineType: action,
      toolType: action,
      controlType: action,
    } as any);
  };

  const handleColorChange = async (newColor: string) => {
    dispatch(setColor(newColor));

    if (tool === 'eraser') {
      dispatch(setTool('brush'));
    }

    if (board?.id) {
      try {
        await historyApi.createAction({
          boardId: board.id,
          actionType: 'COLOR_CHANGE',
          details: `Цвет: ${newColor}`,
        });
      } catch (error) {
        console.warn('Не удалось сохранить смену цвета в историю:', error);
      }
    }

    dispatch(logAction(`Выбрал цвет: ${newColor.toUpperCase()}`));
  };

  const handleBrushSizeChange = (size: number) => {
    dispatch(setBrushSize(size));
  };

  const handleBrushSizeLog = async () => {
    if (board?.id) {
      try {
        await historyApi.createAction({
          boardId: board.id,
          actionType: 'TOOL_CHANGE',
          details: `Размер кисти: ${brushSize}`,
        });
      } catch (error) {
        console.warn('Не удалось сохранить размер кисти в историю:', error);
      }
    }

    dispatch(logAction(`Изменил размер кисти на ${brushSize}`));
  };

  const handleToolChange = async (newTool: 'brush' | 'eraser') => {
    dispatch(setTool(newTool));

    const toolName = newTool === 'brush' ? 'кисть' : 'ластик';

    if (board?.id) {
      try {
        await historyApi.createAction({
          boardId: board.id,
          actionType: 'TOOL_CHANGE',
          details: `Инструмент: ${toolName}`,
        });
      } catch (error) {
        console.warn('Не удалось сохранить смену инструмента в историю:', error);
      }
    }

    dispatch(logAction(`Взял ${toolName}`));
  };

  const handleClear = async () => {
    if (!board?.id) return;
    if (!window.confirm('Вы уверены, что хотите очистить холст?')) return;

    dispatch(clearCanvas());
    sendControlAction('clear');

    try {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'CLEAR',
        details: 'Холст очищен',
      });
    } catch (error) {
      console.warn('Не удалось сохранить очистку в историю:', error);
    }

    dispatch(logAction('Очистил холст'));
  };

  const handleUndo = async () => {
    if (!board?.id || lines.length === 0) return;

    const nextLines = lines.slice(0, -1);
    rebuildLines(nextLines);
    sendControlAction('undo');

    try {
      await historyApi.createAction({
        boardId: board.id,
        actionType: 'TOOL_CHANGE',
        details: 'Отмена действия',
      });
    } catch (error) {
      console.warn('Не удалось сохранить undo в историю:', error);
    }

    dispatch(logAction('Отменил действие (Назад)'));
  };

  const toggleColorPicker = (e: React.MouseEvent) => {
    e.stopPropagation();
    setShowColorPicker(!showColorPicker);
  };

  const defaultColors = [
    '#000000',
    '#FF0000',
    '#00FF00',
    '#0000FF',
    '#FFFF00',
    '#FF00FF',
    '#00FFFF',
    '#FFA500',
    '#800080',
    '#FFFFFF',
    '#808080',
    '#A52A2A',
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
            min="1"
            max="50"
            value={brushSize}
            onChange={(e) => handleBrushSizeChange(parseInt(e.target.value, 10))}
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
                border: tool === 'eraser' ? '1px solid #cbd5e0' : 'none',
              }}
            />
          </div>
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Действия</h3>

        <div className="action-buttons">
          <button className="action-button undo" onClick={handleUndo} disabled={lines.length === 0}>
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