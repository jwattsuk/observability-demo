# Distributed Tracing Configuration Issue and Fix

## Problem
No traces appearing in Tempo despite services generating trace IDs and span IDs.

## Root Cause
Spring Boot 3.2's OTLP exporter auto-configuration is not properly configured. The `management.otlp.tracing.endpoint` property needs to be set correctly OR environment variables need to be properly mapped.

## Solution Options

### Option 1: Use Environment Variables (Recommended for Docker)
Instead of configuring in `application.yml`, Spring Boot can auto-configure from environment variables:

In `docker-compose.yml`, change the environment variables from:
```yaml
- OTEL_EXPORTER_OTLP_ENDPOINT=http://tempo:4318
```

To:
```yaml
- MANAGEMENT_OTLP_TRACING_ENDPOINT=http://tempo:4318/v1/traces
```

### Option 2: Fix application.yml
The correct property path in Spring Boot 3.2 is:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://tempo:4318/v1/traces
```

**Note**: The endpoint MUST include the full path `/v1/traces`, not just the base URL.

### Option 3: Add Logging to Diagnose
Enable debug logging to see what's happening:

```yaml
logging:
  level:
    io.micrometer.tracing: DEBUG
    io.opentelemetry: DEBUG
    io.opentelemetry.exporter: TRACE
```

## Verification Steps

After applying the fix:

1. Restart services:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

2. Generate some requests:
   ```bash
   curl http://localhost:8082/api/trades
   ```

3. Check if Tempo received traces (wait 5-10 seconds):
   ```bash
   curl 'http://localhost:3200/api/search?limit=5' | jq '.traces | length'
   ```
   
   Should return a number > 0

4. Check if Prometheus has span metrics:
   ```bash
   curl -s 'http://localhost:9090/api/v1/query?query=tempo_spanmetrics_calls_total' | jq '.data.result | length'
   ```
   
   Should return a number > 0

5. View in Grafana:
   - Go to http://localhost:3001
   - Navigate to "Distributed Tracing Overview" dashboard
   - Should see service map and metrics

## Current Status
- ✅ Services are generating traces (traceId/spanId in logs)
- ❌ Traces are not being exported to Tempo
- ❌ No span metrics in Prometheus
- ❌ Dashboard shows no data

## Next Steps
Apply Option 1 (environment variables) as it's the most reliable for Docker deployments.
