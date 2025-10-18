# Dashboard Data Issues - Analysis and Fix

## Issues Identified

### 1. Application Logs Dashboard - NO DATA ✅ FIXED
**Problem**: Dashboard queries use `{service="..."}` label but Promtail creates `{container_name="..."}` label.

**Root Cause**: Promtail configuration extracts `container_name` from Docker labels, not `service`.

**Fix**: Updated all LogQL queries in `application-logs.json` to use `container_name` instead of `service`:
- `{service=~"trade-service|enrichment-service"}` → `{container_name=~"trade-service|enrichment-service"}`

**Status**: ✅ Dashboard should now show log data

### 2. Distributed Tracing Dashboard - NO DATA ⚠️ PARTIAL
**Problem**: Dashboard relies on `tempo_spanmetrics_*` metrics which are not available in Prometheus.

**Root Cause**: 
1. Tempo's metrics generator IS working (logs show "collecting metrics")  
2. Tempo IS configured to remote_write to Prometheus
3. BUT Tempo's `/metrics` endpoint times out when Prometheus tries to scrape it
4. Remote write from Tempo → Prometheus may not be working correctly

**Observations**:
- Tempo metrics endpoint takes 16-30+ seconds to respond (exceeds 10s scrape timeout)
- Prometheus doesn't log any remote write reception
- No `tempo_spanmetrics_*` metrics exist in Prometheus

**Potential Solutions**:

#### Option A: Use Tempo Data Source Directly (RECOMMENDED)
Instead of relying on span metrics, query Tempo directly for the Service Map panel and use existing `http_server_requests_*` metrics from services for rate/duration/errors.

Pros:
- Service Map will work perfectly with Tempo
- More accurate trace data
- No dependency on span metrics

Cons:
- Need to modify dashboard queries

#### Option B: Fix Tempo Metrics Export
Investigate why Tempo's remote write to Prometheus isn't working.

Possible issues:
- Tempo's metrics generator WAL errors ("Failed to calculate size of wal")
- Remote write not successfully pushing to Prometheus
- Need to check Tempo's metrics generator specific configuration

#### Option C: Increase Scrape Timeout
Increase Prometheus scrape timeout for Tempo to 60s or more.

**Recommendation**: Use Option A - modify dashboard to query Tempo directly for service map and use application metrics for the other panels.

## Next Steps

1. ✅ Application Logs: Changes committed, test in Grafana
2. ⚠️ Distributed Tracing: Decide on approach (A, B, or C)
3. If Option A: Update dashboard queries to use Tempo + application metrics
4. If Option B: Debug Tempo metrics generator and remote write
5. If Option C: Update Prometheus scrape config for Tempo

## Testing

### Application Logs Dashboard
```bash
# Generate some logs
curl http://localhost:8082/api/trades
curl http://localhost:8081/actuator/health

# Check Loki has data with correct labels
curl -s 'http://localhost:3100/loki/api/v1/query?query={container_name="trade-service"}&limit=1'
```

### Distributed Tracing Dashboard
```bash
# Check if span metrics exist
curl -s 'http://localhost:9090/api/v1/query?query=tempo_spanmetrics_calls_total' | jq '.data.result | length'

# Should return 0 currently
```
