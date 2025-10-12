# Database Deployment Scripts

This directory contains PostgreSQL initialization scripts that are automatically executed when the database container starts.

## Scripts Execution Order

Scripts are executed in alphabetical order:

1. `01-create-trade-table.sql` - Creates the main trade table with indexes and triggers
2. Additional scripts can be added with appropriate numbering

## Trade Table Schema

The `trade` table includes the following fields:

- `id` - Auto-incrementing primary key
- `trade_id` - Unique business identifier for the trade
- `counterparty` - Trading counterparty name
- `instrument_type` - Type of derivative (OPTION, SWAP, FUTURE, etc.)
- `underlying_asset` - The underlying asset or currency pair
- `notional_amount` - Trade notional amount
- `currency` - Trade currency
- `strike_price` - Strike price for options (nullable)
- `maturity_date` - Trade maturity date
- `trade_date` - Date when trade was executed
- `direction` - BUY or SELL
- `status` - Trade status (PENDING, CONFIRMED, SETTLED, CANCELLED)
- `enrichment_data` - JSON field for enriched data from enrichment-service
- `created_at` - Record creation timestamp
- `updated_at` - Record last update timestamp (auto-updated via trigger)

## Sample Data

The initialization script includes sample trade records for testing purposes.

## Running the Database

The database will be automatically initialized when running:

```bash
docker-compose up postgresql
```

Or as part of the full application stack:

```bash
docker-compose up
```