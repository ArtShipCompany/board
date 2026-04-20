import React, { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import DrawingBoard from './components/DrawingBoard';
import Toolbar from './components/Toolbar';
import NetworkBanner from './components/NetworkBanner/NetworkBanner';
import { initBoard, syncPendingStrokes } from './store/drawingSlice';
import { useNetworkStatus } from './hooks/useNetworkStatus';
import { AppDispatch } from './store';
import './styles/App.css';

function App() {
  const dispatch = useDispatch<AppDispatch>();
  const isOnline = useNetworkStatus();

  useEffect(() => {
    dispatch(initBoard());
  }, [dispatch]);

  useEffect(() => {
    if (isOnline) {
      dispatch(syncPendingStrokes());
    }
  }, [isOnline, dispatch]);

  return (
    <div className="app">
      <header className="app-header">
        <h1>ARTSHIP</h1>
      </header>
      <main className="app-main">
        <NetworkBanner />
        <Toolbar />
        <DrawingBoard />
      </main>
    </div>
  );
}

export default App;