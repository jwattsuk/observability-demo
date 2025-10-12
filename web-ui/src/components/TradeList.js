import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    Chip,
    Button,
    Alert,
    CircularProgress,
    IconButton,
    Tooltip,
} from '@mui/material';
import {
    Visibility as VisibilityIcon,
    Refresh as RefreshIcon,
    Delete as DeleteIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { tradeService } from '../services/api';

const TradeList = () => {
    const [trades, setTrades] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const fetchTrades = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await tradeService.getAllTrades();
            setTrades(response.data);
        } catch (err) {
            setError('Failed to fetch trades. Please check if the trade service is running.');
            console.error('Error fetching trades:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteTrade = async (tradeId) => {
        if (window.confirm(`Are you sure you want to delete trade ${tradeId}?`)) {
            try {
                await tradeService.deleteTrade(tradeId);
                setTrades(trades.filter(trade => trade.tradeId !== tradeId));
            } catch (err) {
                setError('Failed to delete trade.');
                console.error('Error deleting trade:', err);
            }
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'CONFIRMED':
                return 'success';
            case 'PENDING':
                return 'warning';
            case 'SETTLED':
                return 'info';
            case 'CANCELLED':
                return 'error';
            default:
                return 'default';
        }
    };

    const formatCurrency = (amount, currency) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency || 'USD',
        }).format(amount);
    };

    useEffect(() => {
        fetchTrades();
    }, []);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4" component="h1">
                    Trades
                </Typography>
                <Button
                    variant="outlined"
                    startIcon={<RefreshIcon />}
                    onClick={fetchTrades}
                >
                    Refresh
                </Button>
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Trade ID</TableCell>
                            <TableCell>Counterparty</TableCell>
                            <TableCell>Instrument</TableCell>
                            <TableCell>Underlying</TableCell>
                            <TableCell>Notional</TableCell>
                            <TableCell>Direction</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Maturity Date</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {trades.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={9} align="center">
                                    <Typography variant="body2" color="text.secondary">
                                        No trades found. Create your first trade!
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            trades.map((trade) => (
                                <TableRow key={trade.id} hover>
                                    <TableCell>
                                        <Typography variant="body2" fontWeight="bold">
                                            {trade.tradeId}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>{trade.counterparty}</TableCell>
                                    <TableCell>{trade.instrumentType}</TableCell>
                                    <TableCell>{trade.underlyingAsset}</TableCell>
                                    <TableCell>
                                        {formatCurrency(trade.notionalAmount, trade.currency)}
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={trade.direction}
                                            color={trade.direction === 'BUY' ? 'primary' : 'secondary'}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <Chip
                                            label={trade.status}
                                            color={getStatusColor(trade.status)}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        {format(new Date(trade.maturityDate), 'MMM dd, yyyy')}
                                    </TableCell>
                                    <TableCell>
                                        <Box display="flex" gap={1}>
                                            <Tooltip title="View Details">
                                                <IconButton
                                                    size="small"
                                                    onClick={() => navigate(`/trades/${trade.tradeId}`)}
                                                >
                                                    <VisibilityIcon />
                                                </IconButton>
                                            </Tooltip>
                                            <Tooltip title="Delete Trade">
                                                <IconButton
                                                    size="small"
                                                    color="error"
                                                    onClick={() => handleDeleteTrade(trade.tradeId)}
                                                >
                                                    <DeleteIcon />
                                                </IconButton>
                                            </Tooltip>
                                        </Box>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default TradeList;