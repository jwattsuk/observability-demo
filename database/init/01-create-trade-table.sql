-- Create the trade table with all necessary fields for a derivative trade
CREATE TABLE IF NOT EXISTS trade (
    id BIGSERIAL PRIMARY KEY,
    trade_id VARCHAR(50) UNIQUE NOT NULL,
    counterparty VARCHAR(100) NOT NULL,
    instrument_type VARCHAR(50) NOT NULL,
    underlying_asset VARCHAR(100) NOT NULL,
    notional_amount DECIMAL(20, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    strike_price DECIMAL(20, 4),
    maturity_date DATE NOT NULL,
    trade_date DATE NOT NULL DEFAULT CURRENT_DATE,
    direction VARCHAR(10) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'SETTLED', 'CANCELLED')),
    enrichment_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_trade_trade_id ON trade(trade_id);
CREATE INDEX IF NOT EXISTS idx_trade_counterparty ON trade(counterparty);
CREATE INDEX IF NOT EXISTS idx_trade_status ON trade(status);
CREATE INDEX IF NOT EXISTS idx_trade_created_at ON trade(created_at);
CREATE INDEX IF NOT EXISTS idx_trade_maturity_date ON trade(maturity_date);

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update the updated_at field
DROP TRIGGER IF EXISTS update_trade_updated_at ON trade;
CREATE TRIGGER update_trade_updated_at
    BEFORE UPDATE ON trade
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert some sample data for testing
INSERT INTO trade (
    trade_id, 
    counterparty, 
    instrument_type, 
    underlying_asset, 
    notional_amount, 
    currency, 
    strike_price, 
    maturity_date, 
    direction, 
    status
) VALUES 
(
    'TRD-001-2024', 
    'Goldman Sachs', 
    'OPTION', 
    'AAPL', 
    1000000.00, 
    'USD', 
    150.00, 
    '2024-12-31', 
    'BUY', 
    'CONFIRMED'
),
(
    'TRD-002-2024', 
    'JPMorgan Chase', 
    'SWAP', 
    'EUR/USD', 
    5000000.00, 
    'EUR', 
    NULL, 
    '2025-06-30', 
    'SELL', 
    'PENDING'
),
(
    'TRD-003-2024', 
    'Morgan Stanley', 
    'FUTURE', 
    'WTI_CRUDE', 
    2500000.00, 
    'USD', 
    75.50, 
    '2024-11-15', 
    'BUY', 
    'CONFIRMED'
);

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON TABLE trade TO tradeuser;
GRANT USAGE, SELECT ON SEQUENCE trade_id_seq TO tradeuser;