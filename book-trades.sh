#!/bin/bash

# Script to book multiple trades for testing
# Usage: ./book-trades.sh [number_of_trades]
# Example: ./book-trades.sh 10

# Default number of trades if not specified
NUM_TRADES=${1:-5}

# Trade service endpoint
TRADE_SERVICE_URL="http://localhost:8082/api/trades"

# Arrays for random data generation
COUNTERPARTIES=("Goldman Sachs" "JP Morgan" "Morgan Stanley" "Deutsche Bank" "Barclays" "UBS" "Credit Suisse" "BNP Paribas" "Citigroup" "HSBC")
INSTRUMENT_TYPES=("OPTION" "FUTURE" "SWAP" "FORWARD")
UNDERLYING_ASSETS=("AAPL" "GOOGL" "MSFT" "AMZN" "TSLA" "NVDA" "META" "JPM" "BAC" "WMT")
CURRENCIES=("USD" "EUR" "GBP" "JPY" "CHF")
DIRECTIONS=("BUY" "SELL")

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to generate random number in range
random_in_range() {
    echo $((RANDOM % ($2 - $1 + 1) + $1))
}

# Function to generate future date
generate_maturity_date() {
    days_ahead=$(random_in_range 30 365)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        date -v+${days_ahead}d +%Y-%m-%d
    else
        # Linux
        date -d "+${days_ahead} days" +%Y-%m-%d
    fi
}

# Function to book a single trade
book_trade() {
    trade_num=$1
    trade_id=$(printf "TRD-%s-%04d" "$(date +%Y%m%d)" "$trade_num")
    
    # Get random values
    counterparty="${COUNTERPARTIES[$((RANDOM % ${#COUNTERPARTIES[@]}))]}"
    instrument_type="${INSTRUMENT_TYPES[$((RANDOM % ${#INSTRUMENT_TYPES[@]}))]}"
    underlying_asset="${UNDERLYING_ASSETS[$((RANDOM % ${#UNDERLYING_ASSETS[@]}))]}"
    notional_amount=$(random_in_range 100000 10000000)
    currency="${CURRENCIES[$((RANDOM % ${#CURRENCIES[@]}))]}"
    strike_price=$(random_in_range 50 500)
    maturity_date=$(generate_maturity_date)
    direction="${DIRECTIONS[$((RANDOM % ${#DIRECTIONS[@]}))]}"

    trade_json=$(cat <<EOF
{
    "tradeId": "$trade_id",
    "counterparty": "$counterparty",
    "instrumentType": "$instrument_type",
    "underlyingAsset": "$underlying_asset",
    "notionalAmount": $notional_amount.00,
    "currency": "$currency",
    "strikePrice": $strike_price.00,
    "maturityDate": "$maturity_date",
    "direction": "$direction"
}
EOF
)

    echo -e "${YELLOW}Booking trade $trade_num/$NUM_TRADES: $trade_id${NC}"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$TRADE_SERVICE_URL" \
        -H "Content-Type: application/json" \
        -d "$trade_json")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -eq 201 ] || [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✓ Successfully booked: $trade_id ($instrument_type $direction $underlying_asset)${NC}"
        return 0
    else
        echo -e "${RED}✗ Failed to book trade: HTTP $http_code${NC}"
        echo "$body"
        return 1
    fi
}

# Main execution
echo "=========================================="
echo "Trade Booking Script"
echo "=========================================="
echo "Booking $NUM_TRADES trades to $TRADE_SERVICE_URL"
echo ""

# Check if trade service is available
if ! curl -s -f "http://localhost:8082/actuator/health" > /dev/null 2>&1; then
    echo -e "${RED}Error: Trade service is not available at $TRADE_SERVICE_URL${NC}"
    echo "Please ensure the trade service is running with: docker-compose up -d"
    exit 1
fi

# Book trades
success_count=0
fail_count=0

for i in $(seq 1 "$NUM_TRADES"); do
    if book_trade "$i"; then
        ((success_count++))
    else
        ((fail_count++))
    fi
    
    # Small delay between requests to avoid overwhelming the service
    if [ "$i" -lt "$NUM_TRADES" ]; then
        sleep 0.5
    fi
done

echo ""
echo "=========================================="
echo "Summary"
echo "=========================================="
echo -e "Total trades: $NUM_TRADES"
echo -e "${GREEN}Successful: $success_count${NC}"
if [ "$fail_count" -gt 0 ]; then
    echo -e "${RED}Failed: $fail_count${NC}"
fi
echo ""
echo "View trades at:"
echo "  - Web UI: http://localhost:3000"
echo "  - Grafana Trade Dashboard: http://localhost:3001/d/trade-creation-monitoring"
echo "  - Grafana Distributed Tracing: http://localhost:3001/d/distributed-tracing"
