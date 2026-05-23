import { useEffect } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../store';
import { initBoard, resetDrawing } from '../store/drawingSlice';
import DrawingBoard from '../components/DrawingBoard';
import Toolbar from '../components/Toolbar';
import { ActionHistoryWidget } from '../components/ActionHistory/ActionHistoryWidget';

const InfiniteBoardPage = () => {
  const { boardId } = useParams<{ boardId: string }>();
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
        <h2>Загрузка бесконечной доски...</h2>
      </div>
    );
  }

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        overflow: 'hidden',
        backgroundColor: board.backgroundColor || '#ffffff',
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
          position: 'absolute',
          inset: 0,
          overflow: 'hidden',
        }}
      >
        <DrawingBoard boardId={numericBoardId} mode="infinite" />
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

export default InfiniteBoardPage;