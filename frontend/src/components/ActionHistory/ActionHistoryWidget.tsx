import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../store';
import { toggleHistoryWindow } from '../../store/historySlice';
import './ActionHistoryWidget.css';

export const ActionHistoryWidget: React.FC = () => {
  const dispatch = useDispatch();
  const { events, isOpen } = useSelector((state: RootState) => state.history);
  console.log('[Widget] isOpen:', isOpen, 'events:', events.length);

  return (
    <div className="history-widget-container">

      {isOpen && (
        <div className="history-window">
          <div className="history-header">
            История действий
          </div>

          <div className="history-content">
            {events.length === 0 ? (
              <p className="history-empty">История пока пуста</p>
            ) : (
              <ul className="history-list">
                {events.map((evt) => {
                  console.log('[Widget] Rendering event:', evt);
                  return (
                    <li key={evt.id} className="history-list-item">
                      <span>{evt.actionText}</span>
                      <span className="history-time">{evt.time}</span>
                    </li>
                  );
                })}
              </ul>
            )}
          </div>
        </div>
      )}

      <button
        onClick={() => dispatch(toggleHistoryWindow())}
        className={`history-toggle-btn ${isOpen ? 'open' : ''}`}
      >
        {isOpen ? 'Закрыть' : 'История действий'}
      </button>

    </div>
  );
};