#!/bin/bash

# Build script for the observability demo

set -e

echo "🏗️  Building Observability Demo Services..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker/Rancher Desktop and try again."
    exit 1
fi

print_status "Docker is running ✓"

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH. Please install Maven 3.8+ and try again."
    exit 1
fi

print_status "Maven is available ✓"

# Build all Maven projects
print_status "Building Java services with Maven..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    print_status "Maven build completed successfully ✓"
else
    print_error "Maven build failed ✗"
    exit 1
fi

# Build Docker images
print_status "Building Docker images..."

# Build enrichment-service
print_status "Building enrichment-service Docker image..."
cd enrichment-service
docker build -t observability-demo/enrichment-service:latest .
cd ..

# Build trade-service
print_status "Building trade-service Docker image..."
cd trade-service
docker build -t observability-demo/trade-service:latest .
cd ..

# Build web-ui
print_status "Building web-ui Docker image..."
cd web-ui
docker build -t observability-demo/web-ui:latest .
cd ..

print_status "All Docker images built successfully ✓"

# Verify images
print_status "Verifying Docker images..."
docker images | grep observability-demo

print_status "Build completed successfully! 🎉"
echo ""
print_status "To start the application, run:"
echo "  docker-compose up -d"
echo ""
print_status "To view logs:"
echo "  docker-compose logs -f"
echo ""
print_status "Access points:"
echo "  - Web UI: http://localhost:3000"
echo "  - Trade Service: http://localhost:8082"
echo "  - Enrichment Service: http://localhost:8081"