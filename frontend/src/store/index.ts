import { configureStore } from '@reduxjs/toolkit';
import drawingReducer from './drawingSlice';
import historyReducer from './historySlice';

export const store = configureStore({
  reducer: {
    drawing: drawingReducer,
    history: historyReducer
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;