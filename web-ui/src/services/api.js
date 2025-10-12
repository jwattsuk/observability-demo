import axios from 'axios';

const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: `${BASE_URL}/api`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor for logging
api.interceptors.request.use(
    (config) => {
        console.log(`Making ${config.method.toUpperCase()} request to ${config.url}`);
        return config;
    },
    (error) => {
        console.error('Request error:', error);
        return Promise.reject(error);
    }
);

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => {
        console.log(`Response received from ${response.config.url}:`, response.status);
        return response;
    },
    (error) => {
        console.error('Response error:', error);
        if (error.response) {
            console.error('Error data:', error.response.data);
            console.error('Error status:', error.response.status);
        }
        return Promise.reject(error);
    }
);

export const tradeService = {
    // Get all trades
    getAllTrades: () => api.get('/trades/list'),

    // Get trade by ID
    getTradeById: (id) => api.get(`/trades/${id}`),

    // Get trade by trade ID
    getTradeByTradeId: (tradeId) => api.get(`/trades/trade-id/${tradeId}`),

    // Create new trade
    createTrade: (tradeData) => api.post('/trades', tradeData),

    // Update trade status
    updateTradeStatus: (tradeId, status) => api.put(`/trades/${tradeId}/status?status=${status}`),

    // Delete trade
    deleteTrade: (tradeId) => api.delete(`/trades/${tradeId}`),

    // Get trades by counterparty
    getTradesByCounterparty: (counterparty) => api.get(`/trades/counterparty/${counterparty}`),

    // Get trades by status
    getTradesByStatus: (status) => api.get(`/trades/status/${status}`),
};

export default api;