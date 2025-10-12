FROM eclipse-temurin:17-jre

LABEL maintainer="observability-demo"
LABEL service="trade-service"

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the jar file
COPY target/trade-service-*.jar app.jar

# Change ownership to appuser
RUN chown -R appuser:appuser /app

# Switch to appuser
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]