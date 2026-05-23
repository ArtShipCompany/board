import { useEffect } from 'react';
import { useParams, useNavigate, Navigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../store';
import { initBoard, resetDrawing } from '../store/drawingSlice';
import DrawingBoard from '../components/DrawingBoard';
import Toolbar from '../components/Toolbar';
import { ActionHistoryWidget } from '../components/ActionHistory/ActionHistoryWidget';

export const BoardPage = () => {
  const { boardId } = useParams<{ boardId: string }>();
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();

  const numericBoardId = Number(boardId);
  const board = useSelector((state: RootState) => state.drawing.board);

  useEffect(() => {
    if (!boardId || Number.isNaN(numericBoardId) || numericBoardId <= 0) return;

    dispatch(resetDrawing());
    dispatch(initBoard(numericBoardId));
  }, [boardId, numericBoardId, dispatch]);

  if (!boardId || Number.isNaN(numericBoardId) || numericBoardId <= 0) {
    return <Navigate to="/" replace />;
  }

  if (!board || board.id !== numericBoardId) {
    return (
      <div style={{ padding: '50px', textAlign: 'center' }}>
        <h2>Загрузка доски...</h2>
      </div>
    );
  }

  if (board.isInfinite) {
    return <Navigate to={`/infinite-board/${numericBoardId}`} replace />;
  }

  const currentVisitorId = localStorage.getItem('visitorId');

  if (board.creatorId && board.creatorId !== currentVisitorId) {
    return (
      <div style={{ padding: '50px', textAlign: 'center', color: 'red' }}>
        <h2>Доступ запрещен</h2>
        <p>Это одиночная доска другого пользователя.</p>
        <button onClick={() => navigate('/')}>На главную</button>
      </div>
    );
  }

  const boardWidth = Number(board.width) || 800;
  const boardHeight = Number(board.height) || 600;

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        overflow: 'hidden',
        backgroundColor: '#e9edf2',
      }}
    >
      <div
        style={{
          position: 'absolute',
          left: 20,
          top: 20,
          zIndex: 40,
        }}
      >
        <Toolbar />
      </div>

      <div
        style={{
          width: '100%',
          height: '100%',
          overflow: 'auto',
          padding: '20px 20px 20px 340px',
          boxSizing: 'border-box',
        }}
      >
        <div
          style={{
            width: boardWidth,
            height: boardHeight,
            margin: '0 auto',
            flexShrink: 0,
          }}
        >
          <DrawingBoard boardId={numericBoardId} mode="solo" />
        </div>
      </div>

      <div
        style={{
          position: 'absolute',
          right: 20,
          bottom: 20,
          zIndex: 45,
        }}
      >
        <ActionHistoryWidget />
      </div>
    </div>
  );
};

export default BoardPage;