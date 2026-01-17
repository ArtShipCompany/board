import React from 'react'
import DrawingBoard from './components/DrawingBoard'
import Toolbar from './components/Toolbar'
import { DrawingProvider } from './hooks/useDrawing'

function App() {
  useEffect(() => {
    boardApi.getMyBoard()
      .then(board => {
        console.log('Доска загружена:', board)
      })
      .catch(error => {
        console.warn('Не удалось загрузить доску:', error)
        boardApi.createEmptyBoard()
      })
  }, [])

  return (
    <DrawingProvider>
      <div className="app">
        <header className="app-header">
          <h1>ARTSHIP</h1>
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