import React from 'react';
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Box
} from '@mui/material';
import {
    TrendingUp as TrendingUpIcon,
    Add as AddIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

const Header = () => {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <AppBar position="static">
            <Toolbar>
                <TrendingUpIcon sx={{ mr: 2 }} />
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    Trade Management System
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        color="inherit"
                        onClick={() => navigate('/trades')}
                        variant={location.pathname === '/trades' ? 'outlined' : 'text'}
                    >
                        View Trades
                    </Button>
                    <Button
                        color="inherit"
                        startIcon={<AddIcon />}
                        onClick={() => navigate('/trades/new')}
                        variant={location.pathname === '/trades/new' ? 'outlined' : 'text'}
                    >
                        New Trade
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;