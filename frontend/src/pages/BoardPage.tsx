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
    const board = useSelector((state: RootState) => state.drawing.board);
    const [isModalOpen, setIsModalOpen] = useState(false);

    useEffect(() => {
        if (numericBoardId !== 0) {
            dispatch(resetDrawing());
            dispatch(initBoard(numericBoardId));
        }
    }, [numericBoardId, dispatch]);

    const handleSaveBoard = async (newData: any) => {
        if (!board) return;
        await boardApi.updateBoard(board.id, newData);
        window.location.reload();
    };

    if (!board) return (
        <div style={{ padding: '50px', textAlign: 'center' }}>
            <h2>Загрузка доски...</h2>
            <p>Если загрузка идет слишком долго, проверь вкладку Network (F12).</p>
        </div>
    );

    return (
        <>
            <div style={{
                position: 'fixed',
                top: 20,
                right: 20,
                zIndex: 1000
            }}>
                <button
                    onClick={() => setIsModalOpen(true)}
                    style={{
                        padding: '8px 16px',
                        background: '#2196F3',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer',
                        fontSize: '14px'
                    }}
                >
                    ⚙️
                </button>
            </div>

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