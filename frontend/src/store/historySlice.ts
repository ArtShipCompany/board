import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface HistoryEvent {
  id: string;
  actionText: string;
  time: string;
}

interface HistoryState {
  events: HistoryEvent[];
  isOpen: boolean;
}

const initialState: HistoryState = {
  events: [],
  isOpen: false,
};

const historySlice = createSlice({
  name: 'history',
  initialState,
  reducers: {
    logAction: (state, action: PayloadAction<string>) => {
      const newEvent: HistoryEvent = {
        id: Math.random().toString(36).substr(2, 9),
        actionText: action.payload,
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
      };
      state.events.unshift(newEvent);
      
      if (state.events.length > 50) {
        state.events.pop();
      }
    },
    toggleHistoryWindow: (state) => {
      state.isOpen = !state.isOpen;
    },
  },
});

export const { logAction, toggleHistoryWindow } = historySlice.actions;
export default historySlice.reducer;