import { Routes, Route } from 'react-router-dom';
import { BoardPage } from './pages/BoardPage';
import InfiniteBoardPage from './pages/InfiniteBoardPage';
import NetworkBanner from './components/NetworkBanner/NetworkBanner';
import './styles/App.css';

function App() {
  return (
    <div className="app">
      <NetworkBanner /> 

      <Routes>
        <Route
          path="/infinite-board"
          element={
            <>
              <header className="app-header">
                <h1>ARTSHIP</h1>
              </header>
              <InfiniteBoardPage />
            </>
          } />

        <Route 
          path="/board/:boardId" 
          element={
            <>
              <header className="app-header">
                <h1>ARTSHIP</h1>
              </header>
              <main className="app-main">
                <BoardPage />
              </main>
            </>
          } 
        />
      </Routes>
    </div>
  );
}

export default App;