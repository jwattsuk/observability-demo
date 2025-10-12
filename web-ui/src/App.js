import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Container, Box } from '@mui/material';
import Header from './components/Header';
import TradeList from './components/TradeList';
import TradeForm from './components/TradeForm';
import TradeDetails from './components/TradeDetails';

function App() {
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Header />
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4, flex: 1 }}>
                <Routes>
                    <Route path="/" element={<Navigate to="/trades" replace />} />
                    <Route path="/trades" element={<TradeList />} />
                    <Route path="/trades/new" element={<TradeForm />} />
                    <Route path="/trades/:tradeId" element={<TradeDetails />} />
                </Routes>
            </Container>
        </Box>
    );
}

export default App;