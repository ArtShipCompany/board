import React from 'react'
import DrawingBoard from './components/DrawingBoard'
import Toolbar from './components/Toolbar'
import { DrawingProvider } from './hooks/useDrawing'

function App() {
  return (
    <DrawingProvider>
      <div className="app">
        <header className="app-header">
          <h1>Drawing Board</h1>
        </header>
        <main className="app-main">
          <Toolbar />
          <DrawingBoard />
        </main>
      </div>
    </DrawingProvider>
  )
}

export default App