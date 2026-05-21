import { createSlice, PayloadAction, createAsyncThunk } from '@reduxjs/toolkit';
import { historyApi } from '../api/historyApi';

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

export const loadHistoryFromDB = createAsyncThunk(
    'history/loadFromDB',
    async ({ boardId, visitorId }: { boardId: number; visitorId: string }) => {
        const records = await historyApi.getBoardHistory(boardId);

        const sortedRecords = [...records].sort((a, b) => {
            return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
        });

        const myRecords = sortedRecords.filter(record => 
            record.newData?.includes(`VISITOR:${visitorId}`)
        );

        const last20 = myRecords.slice(0, 20);

        return last20.map((record) => {
            let dateStr = record.timestamp as string;
            if (!dateStr.endsWith('Z')) {
                dateStr += 'Z';
            }

            const timeStr = new Date(dateStr).toLocaleTimeString('ru-RU', {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });

            let text = '';
            if (record.actionType === 'DRAW') text = `Нарисовал штрих`;
            else if (record.actionType === 'ERASE') text = `Стер элемент`;
            else text = `Действие: ${record.actionType}`;

            return { id: record.id.toString(), actionText: text, time: timeStr };
        });
    }
);

const historySlice = createSlice({
    name: 'history',
    initialState,
    reducers: {
        toggleHistoryWindow: (state) => {
            state.isOpen = !state.isOpen;
        },
        addHistoryEvent: (state, action: PayloadAction<HistoryEvent>) => {
            state.events.unshift(action.payload);
            if (state.events.length > 20) {
                state.events.pop();
            }
        },
    },
    extraReducers: (builder) => {
        builder.addCase(loadHistoryFromDB.fulfilled, (state, action) => {
            state.events = action.payload;
        });
    }
});

export const { toggleHistoryWindow, addHistoryEvent } = historySlice.actions;
export default historySlice.reducer;

export const logAction = (actionText: string) => {
    return addHistoryEvent({
        id: crypto.randomUUID?.() || `evt-${Date.now()}-${Math.random().toString(36).slice(2)}`,
        actionText,
        time: new Date().toLocaleTimeString('ru-RU'),
    });
};