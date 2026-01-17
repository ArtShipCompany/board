import React, { useState, useRef, useEffect } from 'react';
import { useDrawing } from '../../hooks/useDrawing';
import './Toolbar.css';

const Toolbar = () => {
  const { state, dispatch, handleUndo: undoAction, handleClearCanvas: clearCanvasAction } = useDrawing();
  const [showColorPicker, setShowColorPicker] = useState(false);
  const colorPickerRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (colorPickerRef.current && !colorPickerRef.current.contains(event.target)) {
        setShowColorPicker(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleColorChange = (color) => {
    dispatch({ type: 'SET_COLOR', payload: color });
    if (state.tool === 'eraser') {
      dispatch({ type: 'SET_TOOL', payload: 'brush' });
    }
  };

  const handleBrushSizeChange = (size) => {
    dispatch({ type: 'SET_BRUSH_SIZE', payload: size });
  };

  const handleToolChange = (tool) => {
    dispatch({ type: 'SET_TOOL', payload: tool });
  };

  const handleClear = () => {
    if (window.confirm('Вы уверены, что хотите очистить слой?')) {
      clearCanvasAction();
    }
  };

  const handleUndoClick = () => {
    undoAction();
  };

  const toggleColorPicker = (e) => {
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
            className={`tool-button ${state.tool === 'brush' ? 'active' : ''}`}
            onClick={() => handleToolChange('brush')}
            title="Brush"
          >
            <span className="tool-icon">🖌️</span>
            Кисть
          </button>
          <button
            className={`tool-button ${state.tool === 'eraser' ? 'active' : ''}`}
            onClick={() => handleToolChange('eraser')}
            title="Eraser"
          >
            <span className="tool-icon">🧹</span>
            Ластик
          </button>
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Цвет</h3>
        <div className="color-picker-section" ref={colorPickerRef}>
          <div className="color-display" onClick={toggleColorPicker}>
            <div
              className="current-color-display"
              style={{ backgroundColor: state.color }}
              title="Click to select color"
            />
            <span className="color-hex">{state.color.toUpperCase()}</span>
            <span className="color-arrow">{showColorPicker ? '▲' : '▼'}</span>
          </div>
          {showColorPicker && (
            <div className="color-picker-modal">
              <div className="color-picker-header">
                <h4>Выбрать цвет</h4>
                <div className="selected-color-info">
                  <span className="selected-color-preview" style={{ backgroundColor: state.color }}></span>
                  <span className="color-hex-value">{state.color.toUpperCase()}</span>
                </div>
              </div>
              <div className="color-wheel-section">
                <input
                  type="color"
                  value={state.color}
                  onChange={(e) => handleColorChange(e.target.value)}
                  className="color-wheel-input"
                  title="Нажмите и перетащите для выбора цвета"
                />
                <div className="color-info">
                  <span>HEX: {state.color.toUpperCase()}</span>
                </div>
              </div>
              <div className="color-slider-container">
                <label>Hue:</label>
                <input
                  type="range"
                  min="0"
                  max="360"
                  value={hueFromColor(state.color)}
                  onChange={(e) => {
                    const hue = parseInt(e.target.value);
                    const newColor = hslToHex(hue, 100, 50);
                    handleColorChange(newColor);
                  }}
                  className="hue-slider"
                  title="Adjust hue"
                />
                <span className="hue-value">{hueFromColor(state.color)}°</span>
              </div>
              <div className="color-slider-container">
                <label>Saturation:</label>
                <input
                  type="range"
                  min="0"
                  max="100"
                  value={saturationFromColor(state.color)}
                  onChange={(e) => {
                    const sat = parseInt(e.target.value);
                    const hue = hueFromColor(state.color);
                    const newColor = hslToHex(hue, sat, 50);
                    handleColorChange(newColor);
                  }}
                  className="saturation-slider"
                  title="Adjust saturation"
                />
                <span className="saturation-value">{saturationFromColor(state.color)}%</span>
              </div>
              <div className="default-colors">
                <h4>Быстрый выбор цвета</h4>
                <div className="default-colors-grid">
                  {defaultColors.map((color) => (
                    <button
                      key={color}
                      className={`default-color-button ${state.color === color ? 'active' : ''}`}
                      style={{ backgroundColor: color }}
                      onClick={() => handleColorChange(color)}
                      title={color}
                    />
                  ))}
                </div>
              </div>
              <button
                className="close-picker-button"
                onClick={() => setShowColorPicker(false)}
              >
                Закрыть
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="toolbar-section">
        <h3>Размер кисти</h3>
        <div className="brush-size-control">
          <div className="brush-size-slider-container">
            <input
              type="range"
              min="1"
              max="50"
              value={state.brushSize}
              onChange={(e) => handleBrushSizeChange(parseInt(e.target.value))}
              className="brush-size-slider"
              title="Adjust brush size"
            />
            <div className="brush-size-value">
              <span>{state.brushSize}px</span>
            </div>
          </div>
          <div className="brush-size-preview">
            <div
              className="brush-preview-circle"
              style={{
                width: `${state.brushSize}px`,
                height: `${state.brushSize}px`,
                backgroundColor: state.tool === 'eraser' ? '#ffffff' : state.color,
                border: state.tool === 'eraser' ? '1px solid #cbd5e0' : 'none'
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
            onClick={handleUndoClick}
            disabled={state.lines.length === 0}
            title="Undo last stroke"
          >
            <span className="action-icon">↶</span>
            Назад
          </button>
          <button
            className="action-button clear"
            onClick={handleClear}
            disabled={state.lines.length === 0 && !state.currentLine}
            title="Clear canvas"
          >
            <span className="action-icon">🗑️</span>
            Очистить
          </button>
        </div>
      </div>
    </div>
  );
};

const hueFromColor = (hexColor) => {
  const r = parseInt(hexColor.slice(1, 3), 16) / 255;
  const g = parseInt(hexColor.slice(3, 5), 16) / 255;
  const b = parseInt(hexColor.slice(5, 7), 16) / 255;
  const max = Math.max(r, g, b);
  const min = Math.min(r, g, b);
  if (max === min) return 0;
  let hue = 0;
  if (max === r) {
    hue = (g - b) / (max - min);
  } else if (max === g) {
    hue = 2 + (b - r) / (max - min);
  } else {
    hue = 4 + (r - g) / (max - min);
  }
  hue = hue * 60;
  if (hue < 0) hue += 360;
  return Math.round(hue);
};

const saturationFromColor = (hexColor) => {
  const r = parseInt(hexColor.slice(1, 3), 16) / 255;
  const g = parseInt(hexColor.slice(3, 5), 16) / 255;
  const b = parseInt(hexColor.slice(5, 7), 16) / 255;
  const max = Math.max(r, g, b);
  const min = Math.min(r, g, b);
  if (max === 0) return 0;
  const saturation = ((max - min) / max) * 100;
  return Math.round(saturation);
};

const hslToHex = (h, s, l) => {
  h /= 360;
  s /= 100;
  l /= 100;
  let r, g, b;
  if (s === 0) {
    r = g = b = l;
  } else {
    const hue2rgb = (p, q, t) => {
      if (t < 0) t += 1;
      if (t > 1) t -= 1;
      if (t < 1/6) return p + (q - p) * 6 * t;
      if (t < 1/2) return q;
      if (t < 2/3) return p + (q - p) * (2/3 - t) * 6;
      return p;
    };
    const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
    const p = 2 * l - q;
    r = hue2rgb(p, q, h + 1/3);
    g = hue2rgb(p, q, h);
    b = hue2rgb(p, q, h - 1/3);
  }
  const toHex = (x) => {
    const hex = Math.round(x * 255).toString(16);
    return hex.length === 1 ? '0' + hex : hex;
  };
  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
};

export default Toolbar;