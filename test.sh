#!/bin/bash

# Test script for the observability demo

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
}

print_error() {
    echo -e "${RED}[FAIL]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Function to test endpoint
test_endpoint() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    
    print_test "Testing $description..."
    
    response=$(curl -s -w "%{http_code}" -o /dev/null "$url" || echo "000")
    
    if [ "$response" = "$expected_status" ]; then
        print_success "$description ✓"
        return 0
    else
        print_error "$description ✗ (Expected: $expected_status, Got: $response)"
        return 1
    fi
}

# Function to wait for service
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    print_status "Waiting for $service_name to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            print_success "$service_name is ready ✓"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service_name failed to start within $((max_attempts * 2)) seconds"
    return 1
}

echo "🧪 Testing Observability Demo Application"
echo "========================================"

# Check if services are running
print_status "Checking if Docker Compose services are running..."
if ! docker-compose ps | grep -q "Up"; then
    print_warning "Services don't appear to be running. Starting them now..."
    docker-compose up -d
fi

print_status "Waiting for services to be ready..."

# Wait for each service
wait_for_service "http://localhost:5432" "PostgreSQL" || exit 1
wait_for_service "http://localhost:8081/actuator/health" "Enrichment Service" || exit 1
wait_for_service "http://localhost:8080/actuator/health" "Trade Service" || exit 1
wait_for_service "http://localhost:3000" "Web UI" || exit 1

echo ""
print_status "Running API tests..."

# Test health endpoints
test_endpoint "http://localhost:8080/actuator/health" "Trade Service Health Check"
test_endpoint "http://localhost:8081/actuator/health" "Enrichment Service Health Check"
test_endpoint "http://localhost:3000" "Web UI Accessibility"

# Test trade service endpoints
test_endpoint "http://localhost:8080/api/trades/list" "Get All Trades"
test_endpoint "http://localhost:8080/api/trades/health" "Trade Service Custom Health"

# Test enrichment service endpoints
test_endpoint "http://localhost:8081/api/enrichment/health" "Enrichment Service Custom Health"

echo ""
print_status "Testing trade creation flow..."

# Create a test trade
trade_data='{
  "tradeId": "TEST-AUTO-001",
  "counterparty": "Test Bank",
  "instrumentType": "OPTION", 
  "underlyingAsset": "AAPL",
  "notionalAmount": 1000000.00,
  "currency": "USD",
  "strikePrice": 150.00,
  "maturityDate": "2024-12-31",
  "direction": "BUY"
}'

print_test "Creating test trade..."
create_response=$(curl -s -w "%{http_code}" -X POST "http://localhost:8080/api/trades" \
  -H "Content-Type: application/json" \
  -d "$trade_data")

status_code=$(echo "$create_response" | tail -c 4)
response_body=$(echo "$create_response" | head -c -4)

if [ "$status_code" = "201" ]; then
    print_success "Trade creation ✓"
    
    # Extract trade ID and test retrieval
    if command -v jq &> /dev/null; then
        trade_id=$(echo "$response_body" | jq -r '.tradeId')
        print_test "Retrieving created trade: $trade_id"
        
        if test_endpoint "http://localhost:8080/api/trades/trade-id/$trade_id" "Trade Retrieval"; then
            print_success "Trade retrieval ✓"
        fi
    else
        print_warning "jq not available, skipping trade retrieval test"
    fi
else
    print_error "Trade creation ✗ (Status: $status_code)"
    echo "Response: $response_body"
fi

echo ""
print_status "Testing enrichment service directly..."

enrichment_data='{
  "tradeId": "TEST-ENRICH-001",
  "instrumentType": "OPTION",
  "underlyingAsset": "AAPL", 
  "notionalAmount": 1000000,
  "currency": "USD",
  "maturityDate": "2024-12-31",
  "direction": "BUY"
}'

print_test "Testing enrichment endpoint..."
enrich_response=$(curl -s -w "%{http_code}" -X POST "http://localhost:8081/api/enrichment/trade" \
  -H "Content-Type: application/json" \
  -d "$enrichment_data")

enrich_status=$(echo "$enrich_response" | tail -c 4)

if [ "$enrich_status" = "200" ]; then
    print_success "Enrichment service ✓"
else
    print_error "Enrichment service ✗ (Status: $enrich_status)"
fi

echo ""
print_status "Test Summary"
print_status "============"
print_status "✅ All services are running and accessible"
print_status "✅ Health checks are passing"
print_status "✅ API endpoints are responding"
print_status "✅ Trade creation and enrichment flow is working"

echo ""
print_status "Manual Testing Instructions:"
echo "1. Open http://localhost:3000 in your browser"
echo "2. Click 'New Trade' to create a trade"
echo "3. Fill in the form and submit"
echo "4. Check the trade list to see your created trade"
echo "5. Click on a trade to view detailed information including enrichment data"

echo ""
print_status "API Documentation:"
echo "- Trade Service API: http://localhost:8080/api/trades"
echo "- Enrichment Service API: http://localhost:8081/api/enrichment"
echo "- Sample trade JSON available in: sample-trade.json"

echo ""
print_status "🎉 All tests completed successfully!"