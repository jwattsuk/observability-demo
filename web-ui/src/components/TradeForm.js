import React, { useState } from 'react';
import {
    Box,
    Paper,
    Typography,
    TextField,
    Button,
    Grid,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    CircularProgress,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { tradeService } from '../services/api';

const TradeForm = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const {
        control,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm({
        defaultValues: {
            tradeId: '',
            counterparty: '',
            instrumentType: '',
            underlyingAsset: '',
            notionalAmount: '',
            currency: 'USD',
            strikePrice: '',
            maturityDate: '',
            direction: 'BUY',
        },
    });

    const instrumentTypes = [
        'OPTION',
        'SWAP',
        'FUTURE',
        'FORWARD',
        'BOND',
        'EQUITY',
    ];

    const currencies = [
        'USD',
        'EUR',
        'GBP',
        'JPY',
        'CHF',
        'CAD',
        'AUD',
    ];

    const directions = [
        { value: 'BUY', label: 'Buy' },
        { value: 'SELL', label: 'Sell' },
    ];

    const onSubmit = async (data) => {
        try {
            setLoading(true);
            setError(null);

            // Convert string values to appropriate types
            const formattedData = {
                ...data,
                notionalAmount: parseFloat(data.notionalAmount),
                strikePrice: data.strikePrice ? parseFloat(data.strikePrice) : null,
            };

            await tradeService.createTrade(formattedData);
            setSuccess(true);

            // Navigate to trade list after a short delay
            setTimeout(() => {
                navigate('/trades');
            }, 2000);

        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create trade. Please try again.');
            console.error('Error creating trade:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        reset();
        setError(null);
        setSuccess(false);
    };

    if (success) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <Alert severity="success" sx={{ width: '100%', maxWidth: 600 }}>
                    Trade created successfully! Redirecting to trade list...
                </Alert>
            </Box>
        );
    }

    return (
        <Box>
            <Typography variant="h4" component="h1" gutterBottom>
                Create New Trade
            </Typography>

            <Paper sx={{ p: 4, mt: 3 }}>
                {error && (
                    <Alert severity="error" sx={{ mb: 3 }}>
                        {error}
                    </Alert>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <Controller
                                name="tradeId"
                                control={control}
                                rules={{ required: 'Trade ID is required' }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Trade ID"
                                        fullWidth
                                        error={!!errors.tradeId}
                                        helperText={errors.tradeId?.message}
                                        placeholder="e.g., TRD-001-2024"
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="counterparty"
                                control={control}
                                rules={{ required: 'Counterparty is required' }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Counterparty"
                                        fullWidth
                                        error={!!errors.counterparty}
                                        helperText={errors.counterparty?.message}
                                        placeholder="e.g., Goldman Sachs"
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="instrumentType"
                                control={control}
                                rules={{ required: 'Instrument type is required' }}
                                render={({ field }) => (
                                    <FormControl fullWidth error={!!errors.instrumentType}>
                                        <InputLabel>Instrument Type</InputLabel>
                                        <Select {...field} label="Instrument Type">
                                            {instrumentTypes.map((type) => (
                                                <MenuItem key={type} value={type}>
                                                    {type}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="underlyingAsset"
                                control={control}
                                rules={{ required: 'Underlying asset is required' }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Underlying Asset"
                                        fullWidth
                                        error={!!errors.underlyingAsset}
                                        helperText={errors.underlyingAsset?.message}
                                        placeholder="e.g., AAPL, EUR/USD, WTI_CRUDE"
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="notionalAmount"
                                control={control}
                                rules={{
                                    required: 'Notional amount is required',
                                    min: { value: 0.01, message: 'Amount must be positive' },
                                }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Notional Amount"
                                        type="number"
                                        fullWidth
                                        error={!!errors.notionalAmount}
                                        helperText={errors.notionalAmount?.message}
                                        inputProps={{ step: '0.01', min: '0' }}
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="currency"
                                control={control}
                                rules={{ required: 'Currency is required' }}
                                render={({ field }) => (
                                    <FormControl fullWidth error={!!errors.currency}>
                                        <InputLabel>Currency</InputLabel>
                                        <Select {...field} label="Currency">
                                            {currencies.map((currency) => (
                                                <MenuItem key={currency} value={currency}>
                                                    {currency}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="strikePrice"
                                control={control}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Strike Price (Optional)"
                                        type="number"
                                        fullWidth
                                        inputProps={{ step: '0.0001', min: '0' }}
                                        helperText="For options and similar instruments"
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} md={6}>
                            <Controller
                                name="direction"
                                control={control}
                                rules={{ required: 'Direction is required' }}
                                render={({ field }) => (
                                    <FormControl fullWidth error={!!errors.direction}>
                                        <InputLabel>Direction</InputLabel>
                                        <Select {...field} label="Direction">
                                            {directions.map((direction) => (
                                                <MenuItem key={direction.value} value={direction.value}>
                                                    {direction.label}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                )}
                            />
                        </Grid>

                        <Grid item xs={12}>
                            <Controller
                                name="maturityDate"
                                control={control}
                                rules={{ required: 'Maturity date is required' }}
                                render={({ field }) => (
                                    <TextField
                                        {...field}
                                        label="Maturity Date"
                                        type="date"
                                        fullWidth
                                        InputLabelProps={{
                                            shrink: true,
                                        }}
                                        error={!!errors.maturityDate}
                                        helperText={errors.maturityDate?.message}
                                    />
                                )}
                            />
                        </Grid>
                    </Grid>

                    <Box sx={{ mt: 4, display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                        <Button
                            variant="outlined"
                            onClick={handleReset}
                            disabled={loading}
                        >
                            Reset
                        </Button>
                        <Button
                            variant="outlined"
                            onClick={() => navigate('/trades')}
                            disabled={loading}
                        >
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={loading}
                            startIcon={loading ? <CircularProgress size={20} /> : null}
                        >
                            {loading ? 'Creating...' : 'Create Trade'}
                        </Button>
                    </Box>
                </form>
            </Paper>
        </Box>
    );
};

export default TradeForm;