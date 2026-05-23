import React from 'react';
import Toolbar from '../components/Toolbar';
import DrawingBoard from '../components/DrawingBoard';
import './InfiniteBoardPage.css';
import { ActionHistoryWidget } from '../components/ActionHistory/ActionHistoryWidget';

const InfiniteBoardPage: React.FC = () => {
  const INFINITE_BOARD_ID = 1; 

  return (
    <div className="infinite-board-page">
      <div className="toolbar-container">
        <Toolbar />
      </div>
      
      <DrawingBoard boardId={INFINITE_BOARD_ID} isInfinite={true} />
      <ActionHistoryWidget />
    </div>
  );
};

export default InfiniteBoardPage;