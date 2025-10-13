# Observability Demo - Trade Management System

A comprehensive microservices-based trade management system built with Spring Boot, PostgreSQL, and React, featuring a complete **observability stack** with **Grafana**, **Tempo**, **Prometheus**, **Loki**, and **Promtail**. This project demonstrates modern observability patterns including metrics collection, distributed tracing, centralized logging, and unified visualization.

## 🏗️ Architecture Overview

### Application Stack
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│                 │    │                  │    │                 │
│   React Web UI  │────│  Trade Service   │────│ Enrichment Svc  │
│   (Port 3000)   │    │   (Port 8082)    │    │   (Port 8081)   │
│                 │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
         │                       │
         │                       │ 
         │              ┌────────┴────────┐
         │              │                 │
         ▼              ▼                 ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Promtail     │    │   PostgreSQL    │    │     Tempo       │
│  (Log Shipping) │    │   Database      │    │   (Traces)      │
└─────────────────┘    │   (Port 5432)   │    └─────────────────┘
         │              └─────────────────┘             │
         │                       │                      │
         │              ┌─────────────────┐             │
         │              │   Prometheus    │             │
         │              │   (Metrics)     │             │
         │              └─────────────────┘             │
         │                       │                      │
         ▼                       │                      │
┌─────────────────┐             │                      │
│      Loki       │◄────────────┼──────────────────────┼──┐
│     (Logs)      │             │                      │  │
└─────────────────┘             │                      │  │
         │                       │                      │  │
         └───────────────────────┼──────────────────────┼──┤
                                 │                      │  │
                                 ▼                      ▼  ▼
                        ┌─────────────────────────────────────┐
                        │            Grafana                  │
                        │     (Unified Observability)        │
                        └─────────────────────────────────────┘
```

### 📊 Observability Stack Components

| Component | Purpose | Port | Details |
|-----------|---------|------|---------|
| **Grafana** | Visualization & Dashboards | 3001 | Unified observability interface |
| **Prometheus** | Metrics Collection & Storage | 9090 | Application & JVM metrics |
| **Tempo** | Distributed Tracing | 3200, 4317, 4318 | OpenTelemetry traces |
| **Loki** | Log Aggregation | 3100 | Centralized logging |
| **Promtail** | Log Collection | - | Ships logs from containers |

## 🚀 Quick Start

### Prerequisites
- **Docker & Docker Compose** (Rancher Desktop or Docker Desktop)
- **Java 17** (for local development)
- **Maven 3.8+** (for builds)
- **Node.js 18+** (for UI development)

### 1. Clone and Build
```bash
git clone <repository-url>
cd observability-demo

# Build all services
mvn clean package -DskipTests
# Or use the build script
./build.sh
```

### 2. Start Complete Stack
```bash
# Start all services including observability stack
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 3. Access All Services

#### Application Services
- **Web UI**: http://localhost:3000
- **Trade Service API**: http://localhost:8082/api/trades
- **Enrichment Service**: http://localhost:8081/api/enrichment
- **PostgreSQL**: localhost:5432 (tradeuser/tradepass)

#### Observability Services
- **Grafana**: http://localhost:3001 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Tempo**: http://localhost:3200
- **Loki**: http://localhost:3100

## 🏢 Service Details

### Trade Service (Port 8082)

Main REST API for trade management with full CRUD operations and automatic enrichment.

#### Key Endpoints:
- `POST /api/trades` - Create new trade (triggers enrichment)
- `GET /api/trades/list` - Get all trades
- `GET /api/trades/{id}` - Get trade by ID
- `GET /api/trades/trade-id/{tradeId}` - Get trade by business ID
- `PUT /api/trades/{tradeId}/status?status={STATUS}` - Update trade status
- `DELETE /api/trades/{tradeId}` - Delete trade
- `GET /actuator/health` - Health check
- `GET /actuator/prometheus` - Metrics endpoint

#### Example Trade Creation:
```bash
curl -X POST http://localhost:8082/api/trades \
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

Simulates external data providers with configurable delays and failure rates for testing resilience patterns.

#### Key Endpoints:
- `POST /api/enrichment/trade` - Enrich trade data
- `GET /actuator/health` - Health check
- `GET /actuator/prometheus` - Metrics endpoint

#### Configuration:
- **Delay Range**: 100ms - 2000ms (configurable)
- **Failure Rate**: 5% (configurable)
- **Mock Data**: Market data, risk metrics, pricing information

#### Example Direct Enrichment Call:
```bash
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

### Database Schema

The PostgreSQL `trade` table includes:
- Basic trade information (ID, counterparty, instrument type)
- Financial details (notional amount, currency, strike price)
- **Enrichment data** (JSONB field for flexible external data storage)
- Status tracking and audit fields (created_at, updated_at)

### Trade Creation Flow

1. **User submits trade** via Web UI or API
2. **Trade Service** validates and saves trade with `PENDING` status
3. **Trade Service** calls Enrichment Service asynchronously
4. **Enrichment Service** returns market data, risk metrics, pricing
5. **Trade Service** updates trade with enrichment data and `CONFIRMED` status
6. **User sees updated trade** in UI with full enrichment data

All steps are fully instrumented with metrics, logs, and distributed traces for complete observability.

## 📈 Observability Features

### 🎯 Metrics (Prometheus + Micrometer)

**Automatically Collected Metrics:**
- HTTP request rates, response times, and error rates
- JVM memory usage, garbage collection, thread counts
- Database connection pool metrics
- Custom business metrics (trade creation rates, enrichment latency)

**Access Metrics:**
```bash
# Spring Boot Prometheus endpoints
curl http://localhost:8082/actuator/prometheus
curl http://localhost:8081/actuator/prometheus

# Prometheus Query UI
open http://localhost:9090
```

**Sample Queries:**
```promql
# Request rate by service
sum(rate(http_server_requests_seconds_count[5m])) by (job)

# 95th percentile response time
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# JVM memory usage
jvm_memory_used_bytes / jvm_memory_max_bytes * 100
```

### 🔍 Distributed Tracing (Tempo + OpenTelemetry)

**Features:**
- End-to-end request tracing across all services
- Automatic instrumentation for HTTP, JPA, WebClient
- Correlation between frontend and backend traces
- Service dependency mapping

**Trace Flow Example:**
1. User action in React UI → 2. HTTP call to Trade Service → 3. Database query → 4. HTTP call to Enrichment Service → 5. Response chain

**View Traces:**
- Access Grafana → Explore → Select Tempo
- Search by service, operation, or trace ID
- View detailed span timelines and service maps

### 📝 Centralized Logging (Loki + Promtail)

**Features:**
- Structured JSON logging in production
- Automatic log collection from all containers
- Correlation with traces via trace IDs
- Log aggregation and search capabilities

**Log Structure:**
```json
{
  "@timestamp": "2025-10-13T15:30:45.123Z",
  "level": "INFO",
  "service": "trade-service",
  "traceId": "abc123def456",
  "spanId": "789xyz",
  "message": "Processing trade creation",
  "trade_id": "TRD-001-2024"
}
```

**Access Logs:**
- Grafana → Explore → Select Loki
- Query: `{service="trade-service"} |= "ERROR"`
- Filter by service, log level, or trace ID

### 📊 Pre-built Dashboards

#### 1. Spring Boot Application Overview
- Request rates and error rates by service
- Response time percentiles (50th, 95th, 99th)
- JVM memory usage and garbage collection
- Thread counts and database connection pools

#### 2. Application Logs Dashboard
- Log volume by service and level
- Error rate trends
- Real-time log streaming
- Log level distribution

#### 3. Distributed Tracing Overview
- Service dependency map
- Request flow visualization  
- Latency analysis by service
- Error rate tracking across the distributed system

## �️ Development Setup

### Local Development with Observability

For development, you can run services locally while using the containerized observability stack:

```bash
# Start only observability stack and database
docker-compose up postgresql prometheus tempo loki promtail grafana -d

# Wait for services to be ready
docker-compose logs -f grafana
```

Then run services locally:

```bash
# Terminal 1 - Enrichment Service (runs on port 8080 locally)
cd enrichment-service
mvn spring-boot:run

# Terminal 2 - Trade Service (runs on port 8080 locally)
cd trade-service
mvn spring-boot:run -Dspring-boot.run.profiles=default

# Terminal 3 - Web UI (runs on port 3000)
cd web-ui
npm install
npm start
```

**Local Service URLs:**
- Web UI: http://localhost:3000
- Trade Service: http://localhost:8080 (not 8082 when running locally)
- Enrichment Service: http://localhost:8081

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

### Environment Configuration

#### Docker Environment Variables
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - Database connection
- `ENRICHMENT_SERVICE_URL` - URL for enrichment service
- `REACT_APP_API_URL` - API URL for React app
- `OTEL_EXPORTER_OTLP_ENDPOINT` - OpenTelemetry collector endpoint

#### Spring Profiles
- **default**: Local development with localhost database and services
- **docker**: Container deployment with environment variables and service discovery

## �🔧 Configuration Details

### Spring Boot Observability Configuration

**Metrics Configuration:**
```yaml
management:
  endpoints.web.exposure.include: health,info,metrics,prometheus
  endpoint.prometheus.enabled: true
  metrics:
    export.prometheus.enabled: true
    distribution:
      percentiles-histogram.http.server.requests: true
      percentiles.http.server.requests: 0.5,0.9,0.95,0.99
  tracing.sampling.probability: 1.0

otel:
  exporter.otlp.endpoint: http://tempo:4318
  resource.attributes: service.name=trade-service,deployment.environment=docker-compose
```

**Structured Logging (Logback):**
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <includeContext>true</includeContext>
    <includeMdc>true</includeMdc>
    <customFields>{"service":"trade-service"}</customFields>
  </encoder>
</appender>
```

### React Frontend Tracing

**OpenTelemetry Web Integration:**
- Automatic instrumentation for fetch/XHR requests
- User interaction tracking
- Frontend-to-backend trace correlation
- Performance monitoring for page loads

## 🧪 Testing the Observability Stack

### 1. Generate Sample Data
```bash
# Create several trades to generate metrics and traces
for i in {1..5}; do
  curl -X POST http://localhost:8082/api/trades \
    -H "Content-Type: application/json" \
    -d "{
      \"tradeId\": \"TRD-00$i-2024\",
      \"counterparty\": \"Bank-$i\",
      \"instrumentType\": \"OPTION\",
      \"underlyingAsset\": \"AAPL\",
      \"notionalAmount\": 100000$i,
      \"currency\": \"USD\",
      \"direction\": \"BUY\",
      \"maturityDate\": \"2024-12-31\"
    }"
done
```

### 2. Observe in Grafana
1. Open http://localhost:3001 (admin/admin)
2. Navigate to pre-built dashboards
3. Explore metrics, logs, and traces
4. Create custom queries and visualizations

### 3. Test Error Scenarios
```bash
# Generate some errors to see error tracking
curl -X GET http://localhost:8082/api/trades/nonexistent-id

# Test enrichment service errors
curl -X POST http://localhost:8081/api/enrichment/trade \
  -H "Content-Type: application/json" \
  -d '{"invalid": "data"}'
```

## 🧪 Additional Testing

### Health Checks
```bash
# Check all application services
curl http://localhost:8082/actuator/health  # Trade Service
curl http://localhost:8081/actuator/health  # Enrichment Service
curl http://localhost:3000                  # React app (should return HTML)

# Check observability services
curl http://localhost:9090/-/healthy        # Prometheus
curl http://localhost:3200/ready            # Tempo
curl http://localhost:3100/ready            # Loki
```

### Sample API Testing Workflow
```bash
# 1. Get all trades (should be empty initially)
curl http://localhost:8082/api/trades/list

# 2. Create a trade using sample data
curl -X POST http://localhost:8082/api/trades \
  -H "Content-Type: application/json" \
  -d @sample-trade.json

# 3. Verify trade was created and enriched
curl http://localhost:8082/api/trades/list

# 4. Get specific trade by business ID
curl http://localhost:8082/api/trades/trade-id/TRD-004-2024

# 5. Update trade status
curl -X PUT http://localhost:8082/api/trades/TRD-004-2024/status?status=SETTLED

# 6. Test direct enrichment service
curl -X POST http://localhost:8081/api/enrichment/trade \
  -H "Content-Type: application/json" \
  -d '{
    "tradeId": "TEST-DIRECT-001",
    "instrumentType": "SWAP",
    "underlyingAsset": "EUR/USD",
    "notionalAmount": 5000000,
    "currency": "EUR",
    "maturityDate": "2025-06-30",
    "direction": "RECEIVE"
  }'
```

## 🛠️ Development & Troubleshooting

### Local Development with Observability
```bash
# Start only observability stack
docker-compose up prometheus tempo loki promtail grafana -d

# Run services locally pointing to containerized observability
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
cd trade-service && mvn spring-boot:run
```

### Common Issues & Solutions

**Prometheus Scraping Issues:**
```bash
# Check Prometheus targets
curl http://localhost:9090/api/v1/targets

# Verify Spring Boot metrics endpoint
curl http://localhost:8082/actuator/prometheus
```

**Loki Log Ingestion:**
```bash
# Check Promtail status
docker-compose logs promtail

# Verify Loki is receiving logs
curl http://localhost:3100/ready
```

**Tempo Trace Collection:**
```bash
# Check Tempo health
curl http://localhost:3200/ready

# Verify OpenTelemetry endpoint
curl http://localhost:4318/v1/traces -X POST -d '{}'
```

### Monitoring Commands
```bash
# Check all observability services
docker-compose ps prometheus tempo loki promtail grafana

# View specific service logs
docker-compose logs -f grafana
docker-compose logs -f prometheus

# Restart observability stack
docker-compose restart prometheus tempo loki promtail grafana
```

### Common Development Issues & Solutions

#### Port Conflicts
```bash
# Check what's running on application ports
lsof -i :3000  # Web UI
lsof -i :8080  # Local services
lsof -i :8081  # Enrichment service (external)
lsof -i :8082  # Trade service (external)
lsof -i :5432  # PostgreSQL

# Kill processes if needed
kill -9 $(lsof -ti:8080)
```

#### Database Connection Issues
```bash
# Wait for database to be ready
docker-compose up postgresql -d
docker-compose logs -f postgresql

# Test database connection
docker exec -it trade-db psql -U tradeuser -d tradedb -c "SELECT version();"

# Check database contents
docker exec -it trade-db psql -U tradeuser -d tradedb -c "SELECT * FROM trade;"
```

#### Service Dependencies
```bash
# Check service startup order
docker-compose up --no-deps trade-service  # Start without dependencies
docker-compose logs trade-service           # Check for connection errors

# Restart specific service with dependencies
docker-compose restart trade-service

# Force recreation of services
docker-compose up -d --force-recreate trade-service
```

#### Observability Troubleshooting
```bash
# Check if applications are exposing metrics
curl -s http://localhost:8082/actuator/prometheus | grep -E '^http_server_requests_seconds_count'
curl -s http://localhost:8081/actuator/prometheus | grep -E '^http_server_requests_seconds_count'

# Verify Prometheus is scraping targets
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.health != "up")'

# Check Tempo is receiving traces  
curl http://localhost:3200/api/echo

# Verify Loki is receiving logs
curl -G "http://localhost:3100/loki/api/v1/query" --data-urlencode 'query={service="trade-service"}'
```

### Useful Debugging Commands
```bash
# View all container logs
docker-compose logs --tail=50 -f

# View logs for specific services
docker-compose logs -f trade-service enrichment-service

# Check container resource usage
docker stats

# Access container shells for debugging
docker exec -it trade-service /bin/bash
docker exec -it enrichment-service /bin/bash

# Clean restart everything
docker-compose down -v  # Remove volumes too
docker-compose up -d

# Check network connectivity between services
docker exec trade-service ping enrichment-service
docker exec trade-service curl http://enrichment-service:8080/actuator/health
```

## 🎯 Key Observability Benefits Demonstrated

1. **Full-Stack Visibility**: From frontend user interactions to database queries
2. **Rapid Troubleshooting**: Correlate logs, metrics, and traces in one interface
3. **Performance Monitoring**: Identify bottlenecks and optimization opportunities
4. **Proactive Alerting**: Built-in alert rules for error rates and response times
5. **Business Intelligence**: Track trade processing rates and enrichment success rates

## 🏗️ Architecture & Error Handling

### Resilience Patterns Demonstrated

**Circuit Breaker Behavior:**
- Enrichment service failures don't block trade creation
- Trades remain in `PENDING` status when enrichment fails
- Failed enrichment attempts are logged with full trace context
- Retry mechanisms preserve trace correlation

**Error Handling Strategy:**
- **Trade Service**: Validates input, handles database constraints, manages enrichment timeouts
- **Enrichment Service**: Simulates realistic failure scenarios (5% failure rate)
- **Web UI**: Displays appropriate error messages with correlation IDs
- **Database**: Constraint violations are caught and returned as meaningful error responses

**Observability During Failures:**
- All errors generate metrics (`http_server_requests_seconds_count{status="500"}`)
- Error logs include trace IDs for correlation
- Failed operations create spans with error status in distributed traces
- Error rates visible in Grafana dashboards with alerting thresholds

### Production Readiness Features

- **Health Checks**: All services expose `/actuator/health` endpoints
- **Graceful Shutdown**: Spring Boot handles SIGTERM properly
- **Resource Limits**: Container resource constraints defined
- **Security**: Database credentials externalized via environment variables
- **Monitoring**: Comprehensive metrics for SLA monitoring
- **Audit Trail**: All trade operations logged with timestamps and user context

## 📚 Learning Outcomes

This demo showcases:
- **Three Pillars of Observability**: Metrics, Logs, and Traces working together
- **OpenTelemetry Integration**: Modern, vendor-neutral observability instrumentation
- **Production-Ready Patterns**: Structured logging, sampling strategies, correlation IDs
- **Visualization Best Practices**: Meaningful dashboards and alert configuration
- **Microservices Observability**: Distributed system monitoring and troubleshooting

## 🔗 Useful Resources

- **OpenTelemetry**: https://opentelemetry.io/
- **Grafana Documentation**: https://grafana.com/docs/
- **Prometheus Query Language**: https://prometheus.io/docs/prometheus/latest/querying/
- **Micrometer Metrics**: https://micrometer.io/docs
- **Loki LogQL**: https://grafana.com/docs/loki/latest/logql/

---

**Note**: This observability stack is production-ready and demonstrates industry-standard patterns for monitoring distributed applications. The configuration can be adapted for any Spring Boot microservices architecture.