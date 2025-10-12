package com.jwattsuk.trade.controller;

import com.jwattsuk.trade.dto.TradeCreateRequest;
import com.jwattsuk.trade.dto.TradeResponse;
import com.jwattsuk.trade.entity.TradeStatus;
import com.jwattsuk.trade.service.TradeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "*")
public class TradeController {

    private static final Logger LOG = LoggerFactory.getLogger(TradeController.class);

    private final TradeService tradeService;

    public TradeController(final TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<TradeResponse> createTrade(@Valid @RequestBody final TradeCreateRequest request) {
        LOG.info("Received request to create trade: {}", request.getTradeId());
        
        try {
            var response = tradeService.createTrade(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (final IllegalArgumentException e) {
            LOG.error("Invalid trade creation request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (final Exception e) {
            LOG.error("Error creating trade: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> getTradeById(@PathVariable final Long id) {
        LOG.debug("Received request to get trade by ID: {}", id);
        
        try {
            var response = tradeService.getTradeById(id);
            return ResponseEntity.ok(response);
        } catch (final IllegalArgumentException e) {
            LOG.warn("Trade not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (final Exception e) {
            LOG.error("Error retrieving trade by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trade-id/{tradeId}")
    public ResponseEntity<TradeResponse> getTradeByTradeId(@PathVariable final String tradeId) {
        LOG.debug("Received request to get trade by trade ID: {}", tradeId);
        
        try {
            var response = tradeService.getTradeByTradeId(tradeId);
            return ResponseEntity.ok(response);
        } catch (final IllegalArgumentException e) {
            LOG.warn("Trade not found with trade ID: {}", tradeId);
            return ResponseEntity.notFound().build();
        } catch (final Exception e) {
            LOG.error("Error retrieving trade by trade ID {}: {}", tradeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<TradeResponse>> getAllTrades(final Pageable pageable) {
        LOG.debug("Received request to get all trades with pagination");
        
        try {
            var response = tradeService.getAllTrades(pageable);
            return ResponseEntity.ok(response);
        } catch (final Exception e) {
            LOG.error("Error retrieving trades: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<TradeResponse>> getAllTradesList() {
        LOG.debug("Received request to get all trades as list");
        
        try {
            var response = tradeService.getAllTrades();
            return ResponseEntity.ok(response);
        } catch (final Exception e) {
            LOG.error("Error retrieving trades list: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/counterparty/{counterparty}")
    public ResponseEntity<List<TradeResponse>> getTradesByCounterparty(@PathVariable final String counterparty) {
        LOG.debug("Received request to get trades for counterparty: {}", counterparty);
        
        try {
            var response = tradeService.getTradesByCounterparty(counterparty);
            return ResponseEntity.ok(response);
        } catch (final Exception e) {
            LOG.error("Error retrieving trades for counterparty {}: {}", counterparty, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TradeResponse>> getTradesByStatus(@PathVariable final TradeStatus status) {
        LOG.debug("Received request to get trades with status: {}", status);
        
        try {
            var response = tradeService.getTradesByStatus(status);
            return ResponseEntity.ok(response);
        } catch (final Exception e) {
            LOG.error("Error retrieving trades with status {}: {}", status, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{tradeId}/status")
    public ResponseEntity<TradeResponse> updateTradeStatus(@PathVariable final String tradeId,
                                                          @RequestParam final TradeStatus status) {
        LOG.info("Received request to update status for trade {} to {}", tradeId, status);
        
        try {
            var response = tradeService.updateTradeStatus(tradeId, status);
            return ResponseEntity.ok(response);
        } catch (final IllegalArgumentException e) {
            LOG.warn("Trade not found with trade ID: {}", tradeId);
            return ResponseEntity.notFound().build();
        } catch (final Exception e) {
            LOG.error("Error updating trade status for {}: {}", tradeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{tradeId}")
    public ResponseEntity<Void> deleteTrade(@PathVariable final String tradeId) {
        LOG.info("Received request to delete trade: {}", tradeId);
        
        try {
            tradeService.deleteTrade(tradeId);
            return ResponseEntity.noContent().build();
        } catch (final IllegalArgumentException e) {
            LOG.warn("Trade not found with trade ID: {}", tradeId);
            return ResponseEntity.notFound().build();
        } catch (final Exception e) {
            LOG.error("Error deleting trade {}: {}", tradeId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "trade-service",
            "timestamp", LocalDateTime.now()
        ));
    }
}