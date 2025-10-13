import { WebTracerProvider, BatchSpanProcessor } from '@opentelemetry/sdk-trace-web';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { Resource } from '@opentelemetry/resources';
import { SemanticResourceAttributes } from '@opentelemetry/semantic-conventions';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { FetchInstrumentation } from '@opentelemetry/instrumentation-fetch';
import { XMLHttpRequestInstrumentation } from '@opentelemetry/instrumentation-xml-http-request';

// Create a tracer provider
const provider = new WebTracerProvider({
    resource: new Resource({
        [SemanticResourceAttributes.SERVICE_NAME]: 'trade-web-ui',
        [SemanticResourceAttributes.SERVICE_VERSION]: '1.0.0-SNAPSHOT',
        [SemanticResourceAttributes.DEPLOYMENT_ENVIRONMENT]: 'docker-compose',
    }),
});

// Configure OTLP exporter to send traces to Tempo
const exporter = new OTLPTraceExporter({
    url: 'http://localhost:4318/v1/traces',
    headers: {},
});

// Add batch span processor
provider.addSpanProcessor(new BatchSpanProcessor(exporter));

// Register the provider
provider.register();

// Register only specific instrumentations that work with webpack 5
registerInstrumentations({
    instrumentations: [
        new FetchInstrumentation({
            // Propagate trace context to backend
            propagateTraceHeaderCorsUrls: [
                /http:\/\/localhost:8082\/.*/,  // trade-service
                /http:\/\/localhost:8081\/.*/,  // enrichment-service
            ],
        }),
        new XMLHttpRequestInstrumentation({
            // Propagate trace context to backend
            propagateTraceHeaderCorsUrls: [
                /http:\/\/localhost:8082\/.*/,  // trade-service
                /http:\/\/localhost:8081\/.*/,  // enrichment-service
            ],
        }),
    ],
});

console.log('OpenTelemetry initialized successfully');

export default provider;