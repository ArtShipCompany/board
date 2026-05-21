import { useEffect, useState } from 'react';
import { Routes, Route } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import DrawingBoard from './components/DrawingBoard';
import Toolbar from './components/Toolbar';
import NetworkBanner from './components/NetworkBanner/NetworkBanner';
import { BoardSettingsModal } from './components/BoardSettingsModal/BoardSettingsModal';
import InfiniteBoardPage from './pages/InfiniteBoardPage';
import { initBoard, syncPendingStrokes } from './store/drawingSlice';
import { boardApi } from './api/boardApi';
import { useNetworkStatus } from './hooks/useNetworkStatus';
import { AppDispatch, RootState } from './store';
import './styles/App.css';

function App() {
  const dispatch = useDispatch<AppDispatch>();
  const isOnline = useNetworkStatus();

  const board = useSelector((state: RootState) => state.drawing.board);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    dispatch(initBoard());
  }, [dispatch]);

  useEffect(() => {
    if (isOnline) {
      dispatch(syncPendingStrokes());
    }
  }, [isOnline, dispatch]);

  const handleSaveBoard = async (newData: any) => {
    if (!board) return;
    try {
      await boardApi.updateBoard(board.id, newData);
      window.location.reload(); 
    } catch (e) {
      alert('Ошибка сохранения настроек');
    }
  };

  const MainAppView = (
    <div className="app">
      <header className="app-header">
        <h1>ARTSHIP</h1>
        {board ? (
          <button className="settings-button" onClick={() => setIsModalOpen(true)}>
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="3" />
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
            </svg>
          </button>
        ) : (
          <div style={{ width: '40px' }} />
        )}
      </header>

      {board && (
        <BoardSettingsModal 
          isOpen={isModalOpen} 
          onClose={() => setIsModalOpen(false)} 
          board={board} 
          onSave={handleSaveBoard}
        />
      )}

      <main className="app-main">
        <NetworkBanner />
        <Toolbar />
        <DrawingBoard />
      </main>
    </div>
  );

  return (
    <Routes>
      <Route path="/" element={MainAppView} />
      
      <Route path="/infinite-board" element={<InfiniteBoardPage />} />
    </Routes>
  );
}

export default App;