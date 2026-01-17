import React from 'react'
import { useDrawing } from '../../hooks/useDrawing'
import './Toolbar.css'

const Toolbar = () => {
  const { state, dispatch } = useDrawing()
  
  const colors = [
    '#000000', '#ffffff', '#ff0000', '#00ff00', '#0000ff',
    '#ffff00', '#ff00ff', '#00ffff', '#ffa500', '#800080'
  ]
  
  const brushSizes = [2, 5, 10, 15, 20]
  
  const handleColorChange = (color) => {
    dispatch({ type: 'SET_COLOR', payload: color })
    if (state.tool === 'eraser') {
      dispatch({ type: 'SET_TOOL', payload: 'brush' })
    }
  }
  
  const handleBrushSizeChange = (size) => {
    dispatch({ type: 'SET_BRUSH_SIZE', payload: size })
  }
  
  const handleToolChange = (tool) => {
    dispatch({ type: 'SET_TOOL', payload: tool })
  }
  
  const handleClear = () => {
    if (window.confirm('Are you sure you want to clear the canvas?')) {
      dispatch({ type: 'CLEAR_CANVAS' })
    }
  }
  
  const handleUndo = () => {
    dispatch({ type: 'UNDO' })
  }
  
  return (
    <div className="toolbar">
      <div className="toolbar-section">
        <h3>Tools</h3>
        <div className="tool-buttons">
          <button
            className={`tool-button ${state.tool === 'brush' ? 'active' : ''}`}
            onClick={() => handleToolChange('brush')}
            title="Brush"
          >
            <span className="tool-icon">🖌️</span>
            Brush
          </button>
          <button
            className={`tool-button ${state.tool === 'eraser' ? 'active' : ''}`}
            onClick={() => handleToolChange('eraser')}
            title="Eraser"
          >
            <span className="tool-icon">🧹</span>
            Eraser
          </button>
        </div>
      </div>
      
      <div className="toolbar-section">
        <h3>Colors</h3>
        <div className="color-picker">
          <div className="color-grid">
            {colors.map((color) => (
              <button
                key={color}
                className={`color-button ${state.color === color ? 'active' : ''}`}
                style={{ backgroundColor: color }}
                onClick={() => handleColorChange(color)}
                title={color}
              />
            ))}
          </div>
          <div className="custom-color">
            <input
              type="color"
              value={state.color}
              onChange={(e) => handleColorChange(e.target.value)}
              title="Custom color"
            />
          </div>
        </div>
      </div>
      
      <div className="toolbar-section">
        <h3>Brush Size</h3>
        <div className="brush-sizes">
          {brushSizes.map((size) => (
            <button
              key={size}
              className={`size-button ${state.brushSize === size ? 'active' : ''}`}
              onClick={() => handleBrushSizeChange(size)}
              title={`Size: ${size}px`}
            >
              <div
                className="size-indicator"
                style={{ width: `${size}px`, height: `${size}px` }}
              />
            </button>
          ))}
        </div>
      </div>
      
      <div className="toolbar-section">
        <h3>Actions</h3>
        <div className="action-buttons">
          <button
            className="action-button undo"
            onClick={handleUndo}
            disabled={state.lines.length === 0}
            title="Undo last stroke"
          >
            <span className="action-icon">↶</span>
            Undo
          </button>
          <button
            className="action-button clear"
            onClick={handleClear}
            disabled={state.lines.length === 0 && !state.currentLine}
            title="Clear canvas"
          >
            <span className="action-icon">🗑️</span>
            Clear
          </button>
        </div>
      </div>
    </div>
  )
}

export default Toolbar