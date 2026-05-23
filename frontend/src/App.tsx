import type { ReactNode } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { BoardPage } from './pages/BoardPage';
import InfiniteBoardPage from './pages/InfiniteBoardPage';
import NetworkBanner from './components/NetworkBanner/NetworkBanner';
import './styles/App.css';

function PageLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="page-layout">
      <header className="app-header">
        <h1>{title}</h1>
      </header>

      <main className="app-main">{children}</main>
    </div>
  );
}

function App() {
  return (
    <div className="app">
      <NetworkBanner />

      <div className="app-routes">
        <Routes>
          <Route path="/" element={<HomePage />} />

          <Route
            path="/board/:boardId"
            element={
              <PageLayout title="ARTSHIP (Одиночная)">
                <BoardPage />
              </PageLayout>
            }
          />

          <Route
            path="/infinite-board/:boardId"
            element={
              <PageLayout title="ARTSHIP (Мультиплеер)">
                <InfiniteBoardPage />
              </PageLayout>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;