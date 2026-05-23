import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { boardApi } from '../api/boardApi';
import '../components/BoardSettingsModal/BoardSettingsModal.css';

export const HomePage: React.FC = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);

    const [formData, setFormData] = useState({
        title: 'Моя новая доска',
        width: 800,
        height: 600,
        backgroundColor: '#ffffff',
        isInfinite: false,
        maxParticipants: 10,
    });

    const [orientation, setOrientation] = useState<'landscape' | 'portrait'>('landscape');

    const handleOrientationChange = (o: 'landscape' | 'portrait') => {
        setOrientation(o);
        setFormData((prev) => ({
            ...prev,
            width: prev.height,
            height: prev.width,
        }));
    };

    const handleCreateBoard = async () => {
        setIsLoading(true);

        try {
            let currentVisitorId = localStorage.getItem('visitorId');

            if (!currentVisitorId) {
                currentVisitorId =
                    typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function'
                        ? crypto.randomUUID()
                        : `visitor-${Date.now()}-${Math.random().toString(16).slice(2)}`;

                localStorage.setItem('visitorId', currentVisitorId);
            }

            const newBoard = await boardApi.createBoard({
                title: formData.title,
                description: formData.title,
                width: formData.width,
                height: formData.height,
                backgroundColor: formData.backgroundColor,
                creatorId: currentVisitorId,
                isInfinite: formData.isInfinite,
            });

            navigate(
                formData.isInfinite
                    ? `/infinite-board/${newBoard.id}`
                    : `/board/${newBoard.id}`
            );
        } catch (error) {
            console.error('Ошибка при создании доски:', error);
            alert('Не удалось создать доску. Проверь подключение к серверу.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div
            className="modal-overlay"
            style={{
                position: 'relative',
                height: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
            }}
        >
            <div className="modal-content">
                <h2>Создать новую доску</h2>

                <div className="form-group">
                    <label>Название доски</label>
                    <input
                        value={formData.title}
                        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    />
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Ширина</label>
                        <input
                            type="number"
                            value={formData.width}
                            disabled={formData.isInfinite}
                            onChange={(e) =>
                                setFormData({ ...formData, width: Number(e.target.value) })
                            }
                        />
                    </div>

                    <div className="form-group">
                        <label>Высота</label>
                        <input
                            type="number"
                            value={formData.height}
                            disabled={formData.isInfinite}
                            onChange={(e) =>
                                setFormData({ ...formData, height: Number(e.target.value) })
                            }
                        />
                    </div>
                </div>

                <div className="form-group">
                    <label>Ориентация</label>
                    <select
                        value={orientation}
                        disabled={formData.isInfinite}
                        onChange={(e) => handleOrientationChange(e.target.value as 'landscape' | 'portrait')}
                    >
                        <option value="landscape">Альбомная</option>
                        <option value="portrait">Книжная</option>
                    </select>
                </div>

                <div className="form-group">
                    <label>Цвет фона</label>
                    <input
                        type="color"
                        value={formData.backgroundColor}
                        onChange={(e) =>
                            setFormData({ ...formData, backgroundColor: e.target.value })
                        }
                    />
                </div>

                <div
                    className="form-group"
                    style={{ marginTop: '20px', borderTop: '1px solid #ccc', paddingTop: '15px' }}
                >
                    <label
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '10px',
                            cursor: 'pointer',
                        }}
                    >
                        <input
                            type="checkbox"
                            checked={formData.isInfinite}
                            onChange={(e) =>
                                setFormData({ ...formData, isInfinite: e.target.checked })
                            }
                            style={{ width: 'auto' }}
                        />
                        Бесконечная доска
                    </label>
                </div>

                {formData.isInfinite && (
                    <div className="form-group">
                        <label>Максимум участников</label>
                        <input
                            type="number"
                            min="2"
                            max="50"
                            value={formData.maxParticipants}
                            onChange={(e) =>
                                setFormData({ ...formData, maxParticipants: Number(e.target.value) })
                            }
                        />
                    </div>
                )}

                <div className="modal-actions" style={{ marginTop: '20px' }}>
                    <button
                        type="button"
                        onClick={handleCreateBoard}
                        disabled={isLoading}
                        style={{
                            width: '100%',
                            backgroundColor: '#4CAF50',
                            color: 'white',
                            padding: '10px',
                            fontSize: '16px',
                        }}
                    >
                        {isLoading ? 'Создание...' : 'Создать и начать рисовать'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default HomePage;