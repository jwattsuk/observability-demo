import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    Grid,
    Chip,
    Card,
    CardContent,
    Alert,
    CircularProgress,
    Button,
    Divider,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { tradeService } from '../services/api';

const TradeDetails = () => {
    const { tradeId } = useParams();
    const navigate = useNavigate();
    const [trade, setTrade] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchTrade = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await tradeService.getTradeByTradeId(tradeId);
            setTrade(response.data);
        } catch (err) {
            setError('Failed to fetch trade details. Trade may not exist.');
            console.error('Error fetching trade:', err);
        } finally {
            setLoading(false);
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

    const formatDateTime = (dateTime) => {
        return format(new Date(dateTime), 'MMM dd, yyyy HH:mm:ss');
    };

    const renderEnrichmentData = (enrichmentData) => {
        if (!enrichmentData) return null;

        return (
            <Card sx={{ mt: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Enrichment Data
                    </Typography>

                    {enrichmentData.error ? (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {enrichmentData.error}
                        </Alert>
                    ) : (
                        <Grid container spacing={2}>
                            {enrichmentData.marketData && (
                                <Grid item xs={12} md={4}>
                                    <Typography variant="subtitle2" gutterBottom>
                                        Market Data
                                    </Typography>
                                    <Box sx={{ pl: 2 }}>
                                        {Object.entries(enrichmentData.marketData).map(([key, value]) => (
                                            <Typography key={key} variant="body2">
                                                <strong>{key}:</strong> {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                                            </Typography>
                                        ))}
                                    </Box>
                                </Grid>
                            )}

                            {enrichmentData.riskMetrics && (
                                <Grid item xs={12} md={4}>
                                    <Typography variant="subtitle2" gutterBottom>
                                        Risk Metrics
                                    </Typography>
                                    <Box sx={{ pl: 2 }}>
                                        {Object.entries(enrichmentData.riskMetrics).map(([key, value]) => (
                                            <Typography key={key} variant="body2">
                                                <strong>{key}:</strong> {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                                            </Typography>
                                        ))}
                                    </Box>
                                </Grid>
                            )}

                            {enrichmentData.pricingData && (
                                <Grid item xs={12} md={4}>
                                    <Typography variant="subtitle2" gutterBottom>
                                        Pricing Data
                                    </Typography>
                                    <Box sx={{ pl: 2 }}>
                                        {Object.entries(enrichmentData.pricingData).map(([key, value]) => (
                                            <Typography key={key} variant="body2">
                                                <strong>{key}:</strong> {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                                            </Typography>
                                        ))}
                                    </Box>
                                </Grid>
                            )}
                        </Grid>
                    )}

                    {enrichmentData.enrichmentStatus && (
                        <Box sx={{ mt: 2 }}>
                            <Chip
                                label={`Status: ${enrichmentData.enrichmentStatus}`}
                                color={enrichmentData.enrichmentStatus === 'SUCCESS' ? 'success' : 'error'}
                            />
                        </Box>
                    )}
                </CardContent>
            </Card>
        );
    };

    useEffect(() => {
        fetchTrade();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tradeId]);

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    if (error || !trade) {
        return (
            <Box>
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
                <Button variant="outlined" onClick={() => navigate('/trades')}>
                    Back to Trades
                </Button>
            </Box>
        );
    }

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h4" component="h1">
                    Trade Details: {trade.tradeId}
                </Typography>
                <Button variant="outlined" onClick={() => navigate('/trades')}>
                    Back to Trades
                </Button>
            </Box>

            <Paper sx={{ p: 4 }}>
                <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                        <Typography variant="h6" gutterBottom>
                            Basic Information
                        </Typography>
                        <Box sx={{ pl: 2 }}>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Trade ID:</strong> {trade.tradeId}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Counterparty:</strong> {trade.counterparty}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Instrument Type:</strong> {trade.instrumentType}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Underlying Asset:</strong> {trade.underlyingAsset}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Direction:</strong>
                                <Chip
                                    label={trade.direction}
                                    color={trade.direction === 'BUY' ? 'primary' : 'secondary'}
                                    size="small"
                                    sx={{ ml: 1 }}
                                />
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Status:</strong>
                                <Chip
                                    label={trade.status}
                                    color={getStatusColor(trade.status)}
                                    size="small"
                                    sx={{ ml: 1 }}
                                />
                            </Typography>
                        </Box>
                    </Grid>

                    <Grid item xs={12} md={6}>
                        <Typography variant="h6" gutterBottom>
                            Financial Details
                        </Typography>
                        <Box sx={{ pl: 2 }}>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Notional Amount:</strong> {formatCurrency(trade.notionalAmount, trade.currency)}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Currency:</strong> {trade.currency}
                            </Typography>
                            {trade.strikePrice && (
                                <Typography variant="body1" sx={{ mb: 1 }}>
                                    <strong>Strike Price:</strong> {formatCurrency(trade.strikePrice, trade.currency)}
                                </Typography>
                            )}
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Trade Date:</strong> {format(new Date(trade.tradeDate), 'MMM dd, yyyy')}
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 1 }}>
                                <strong>Maturity Date:</strong> {format(new Date(trade.maturityDate), 'MMM dd, yyyy')}
                            </Typography>
                        </Box>
                    </Grid>

                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} />
                        <Typography variant="h6" gutterBottom>
                            Audit Information
                        </Typography>
                        <Box sx={{ pl: 2 }}>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                                <strong>Created:</strong> {formatDateTime(trade.createdAt)}
                            </Typography>
                            <Typography variant="body2" sx={{ mb: 1 }}>
                                <strong>Last Updated:</strong> {formatDateTime(trade.updatedAt)}
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>

                {trade.enrichmentData && renderEnrichmentData(trade.enrichmentData)}
            </Paper>
        </Box>
    );
};

export default TradeDetails;