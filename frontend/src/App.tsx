import { Routes, Route } from 'react-router-dom';
import { BoardPage } from './pages/BoardPage';
import InfiniteBoardPage from './pages/InfiniteBoardPage';
import NetworkBanner from './components/NetworkBanner/NetworkBanner';
import './styles/App.css';

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>ARTSHIP</h1>
      </header>
      <NetworkBanner /> 

      <main className="app-main">
        <Routes>
          <Route path="/board/:boardId" element={<BoardPage />} />
          <Route path="/infinite-board" element={<InfiniteBoardPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;