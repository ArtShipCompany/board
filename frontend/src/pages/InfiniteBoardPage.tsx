import React from 'react';
import Toolbar from '../components/Toolbar';
import DrawingBoard from '../components/DrawingBoard';
import './InfiniteBoardPage.css';

const InfiniteBoardPage: React.FC = () => {
  return (
    <div className="infinite-board-page">
      <div className="toolbar-container">
        <Toolbar />
      </div>
      
      <DrawingBoard isInfinite={true} /> 
    </div>
  );
};

export default InfiniteBoardPage;