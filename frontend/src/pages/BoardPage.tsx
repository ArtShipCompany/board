import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../store';
import { initBoard, resetDrawing } from '../store/drawingSlice';
import DrawingBoard from '../components/DrawingBoard';
import Toolbar from '../components/Toolbar';
import { ActionHistoryWidget } from '../components/ActionHistory/ActionHistoryWidget';
import { BoardSettingsModal } from '../components/BoardSettingsModal/BoardSettingsModal';
import { boardApi } from '../api/boardApi';

export const BoardPage = () => {
    const { boardId } = useParams<{ boardId: string }>();
    const dispatch = useDispatch<AppDispatch>();

    const numericBoardId = boardId ? Number(boardId) : 0;
    useEffect(() => {
        if (boardId && Number(boardId) !== 0) {
            dispatch(resetDrawing());
            dispatch(initBoard(numericBoardId));
        }
    }, [numericBoardId, dispatch]);

    const board = useSelector((state: RootState) => state.drawing.board);
    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        if (boardId) {
            dispatch(resetDrawing());
            dispatch(initBoard(numericBoardId));
        }
    }, [boardId, dispatch]);

    const handleSaveBoard = async (newData: any) => {
        if (!board) return;
        await boardApi.updateBoard(board.id, newData);
        window.location.reload(); 
    };

    if (!board) return <div>Загрузка доски...</div>;

    return (
        <>
        <button onClick={() => setIsModalOpen(true)}>Настройки доски</button>
        
        <BoardSettingsModal 
            isOpen={isModalOpen} 
            onClose={() => setIsModalOpen(false)} 
            board={board} 
            onSave={handleSaveBoard}
        />

        <Toolbar />
        <DrawingBoard boardId={numericBoardId} isInfinite={false} />
        <ActionHistoryWidget />
        </>
  );
};