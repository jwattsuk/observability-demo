# Observability Demo - Trade Management System

A comprehensive microservices-based trade management system built with SpringBoot, PostgreSQL, and React, designed to demonstrate observability patterns and modern application architecture.

## Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│                 │    │                  │    │                 │
│   React Web UI  │────│  Trade Service   │────│ Enrichment Svc  │
│   (Port 3000)   │    │   (Port 8080)    │    │   (Port 8081)   │
│                 │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                                │
                       ┌─────────────────┐
                       │                 │
                       │  PostgreSQL DB  │
                       │   (Port 5432)   │
                       │                 │
                       └─────────────────┘
```

### Components

1. **Trade Service**: Main SpringBoot REST API with CRUD operations for derivative trades
2. **Enrichment Service**: Simulates external dependencies with configurable delays and transformations
3. **PostgreSQL Database**: Stores trade data with JSON enrichment fields
4. **React Web UI**: Modern frontend for trade capture and management

## Prerequisites

- **Rancher Desktop** (or Docker Desktop) with Docker Compose support
- **Java 17** (for local development)
- **Maven 3.8+** (for local builds)
- **Node.js 18+** (for UI development)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd observability-demo

# Build all services
mvn clean package -DskipTests
```

### 2. Start with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 3. Access the Application

- **Web UI**: http://localhost:3000
- **Trade Service API**: http://localhost:8080
- **Enrichment Service API**: http://localhost:8081
- **PostgreSQL**: localhost:5432 (tradeuser/tradepass)

## Service Details

### Trade Service (Port 8080)

Main API for trade management with full CRUD operations.

#### Key Endpoints:
- `POST /api/trades` - Create new trade (triggers enrichment)
- `GET /api/trades/list` - Get all trades
- `GET /api/trades/{id}` - Get trade by ID
- `GET /api/trades/trade-id/{tradeId}` - Get trade by business ID
- `PUT /api/trades/{tradeId}/status?status={STATUS}` - Update trade status
- `DELETE /api/trades/{tradeId}` - Delete trade
- `GET /actuator/health` - Health check

#### Example Trade Creation:
```bash
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -d '{
    "tradeId": "TRD-004-2024",
    "counterparty": "Deutsche Bank",
    "instrumentType": "OPTION",
    "underlyingAsset": "GOOGL",
    "notionalAmount": 2000000.00,
    "currency": "USD",
    "strikePrice": 120.00,
    "maturityDate": "2024-12-31",
    "direction": "BUY"
  }'
```

### Enrichment Service (Port 8081)

Simulates external data providers with configurable delays and failure rates.

#### Key Endpoints:
- `POST /api/enrichment/trade` - Enrich trade data
- `GET /api/enrichment/health` - Health check

#### Configuration:
- **Delay Range**: 100ms - 2000ms (configurable)
- **Failure Rate**: 5% (configurable)
- **Mock Data**: Market data, risk metrics, pricing

### Database Schema

The `trade` table includes:
- Basic trade information (ID, counterparty, instrument, etc.)
- Financial details (notional, currency, strike price)
- Enrichment data (JSONB field for flexible data storage)
- Audit fields (created_at, updated_at)

## Development

### Local Development Setup

1. **Start PostgreSQL only:**
   ```bash
   docker-compose up postgresql -d
   ```

2. **Run Services Locally:**
   ```bash
   # Terminal 1 - Enrichment Service
   cd enrichment-service
   mvn spring-boot:run

   # Terminal 2 - Trade Service
   cd trade-service
   mvn spring-boot:run -Dspring-boot.run.profiles=default

   # Terminal 3 - Web UI
   cd web-ui
   npm install
   npm start
   ```

### Building Individual Services

```bash
# Build enrichment-service
cd enrichment-service
mvn clean package
docker build -t enrichment-service .

# Build trade-service
cd trade-service
mvn clean package
docker build -t trade-service .

# Build web-ui
cd web-ui
npm install
npm run build
docker build -t trade-web-ui .
```

## Testing

### Health Checks

```bash
# Check all services
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:3000  # Should return React app
```

### Sample API Calls

```bash
# Get all trades
curl http://localhost:8080/api/trades/list

# Create a trade
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -d @sample-trade.json

# Get enrichment (direct call)
curl -X POST http://localhost:8081/api/enrichment/trade \
  -H "Content-Type: application/json" \
  -d '{
    "tradeId": "TEST-001",
    "instrumentType": "OPTION",
    "underlyingAsset": "AAPL",
    "notionalAmount": 1000000,
    "currency": "USD",
    "maturityDate": "2024-12-31",
    "direction": "BUY"
  }'
```

## Configuration

### Environment Variables (Docker)

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - Database connection
- `ENRICHMENT_SERVICE_URL` - URL for enrichment service
- `REACT_APP_API_URL` - API URL for React app

### Profiles

- **default**: Local development with localhost database
- **docker**: Container deployment with environment variables

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure ports 3000, 8080, 8081, 5432 are available
2. **Database Connection**: Wait for PostgreSQL health check to pass
3. **Service Startup**: Check logs with `docker-compose logs [service-name]`

### Useful Commands

```bash
# View logs
docker-compose logs -f trade-service
docker-compose logs -f enrichment-service

# Restart specific service
docker-compose restart trade-service

# Clean restart
docker-compose down
docker-compose up -d

# Check container status
docker-compose ps
```

## Architecture Notes

### Trade Creation Flow

1. User submits trade via Web UI
2. Trade Service validates and saves trade with PENDING status
3. Trade Service calls Enrichment Service
4. Enrichment Service returns market data, risk metrics, pricing
5. Trade Service updates trade with enrichment data and CONFIRMED status
6. User sees updated trade in UI

### Error Handling

- Enrichment failures keep trade in PENDING status
- Failed enrichment data is stored for debugging
- UI displays appropriate error messages
- Services include health checks and proper logging

### Observability Features

- Structured logging with correlation IDs
- Health check endpoints
- Actuator endpoints for metrics
- Request/response logging
- Error tracking and debugging information

## Future Enhancements

- Add metrics collection (Prometheus/Micrometer)
- Implement distributed tracing (Jaeger/Zipkin)
- Add centralized logging (ELK stack)
- Include performance monitoring
- Add authentication and authorization
- Implement circuit breakers for resilience

## License

This project is for demonstration purposes only.