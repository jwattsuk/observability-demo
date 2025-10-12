package com.jwattsuk.trade.service;

import com.jwattsuk.trade.client.EnrichmentClient;
import com.jwattsuk.trade.dto.TradeCreateRequest;
import com.jwattsuk.trade.dto.TradeResponse;
import com.jwattsuk.trade.entity.Trade;
import com.jwattsuk.trade.entity.TradeStatus;
import com.jwattsuk.trade.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TradeService {

    private static final Logger LOG = LoggerFactory.getLogger(TradeService.class);

    private final TradeRepository tradeRepository;
    private final EnrichmentClient enrichmentClient;

    public TradeService(final TradeRepository tradeRepository, final EnrichmentClient enrichmentClient) {
        this.tradeRepository = tradeRepository;
        this.enrichmentClient = enrichmentClient;
    }

    public TradeResponse createTrade(final TradeCreateRequest request) {
        LOG.info("Creating new trade with ID: {}", request.getTradeId());

        // Check if trade ID already exists
        if (tradeRepository.existsByTradeId(request.getTradeId())) {
            throw new IllegalArgumentException("Trade with ID " + request.getTradeId() + " already exists");
        }

        var trade = mapToEntity(request);
        
        // Save the trade first with PENDING status
        var savedTrade = tradeRepository.save(trade);
        LOG.debug("Trade saved with PENDING status: {}", savedTrade.getTradeId());

        try {
            // Call enrichment service
            var enrichmentData = enrichTrade(request);
            
            // Update trade with enrichment data and confirm status
            savedTrade.setEnrichmentData(enrichmentData);
            savedTrade.setStatus(TradeStatus.CONFIRMED);
            savedTrade.setUpdatedAt(LocalDateTime.now());
            
            savedTrade = tradeRepository.save(savedTrade);
            LOG.info("Trade {} successfully enriched and confirmed", savedTrade.getTradeId());
            
        } catch (final Exception e) {
            LOG.error("Failed to enrich trade {}: {}", savedTrade.getTradeId(), e.getMessage(), e);
            // Keep the trade in database but mark as failed enrichment
            savedTrade.setEnrichmentData(Map.of("error", "Enrichment failed: " + e.getMessage()));
            savedTrade.setStatus(TradeStatus.PENDING);
            savedTrade = tradeRepository.save(savedTrade);
        }

        return mapToResponse(savedTrade);
    }

    @Transactional(readOnly = true)
    public TradeResponse getTradeById(final Long id) {
        LOG.debug("Retrieving trade by ID: {}", id);
        
        var trade = tradeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trade not found with ID: " + id));
        
        return mapToResponse(trade);
    }

    @Transactional(readOnly = true)
    public TradeResponse getTradeByTradeId(final String tradeId) {
        LOG.debug("Retrieving trade by trade ID: {}", tradeId);
        
        var trade = tradeRepository.findByTradeId(tradeId)
            .orElseThrow(() -> new IllegalArgumentException("Trade not found with trade ID: " + tradeId));
        
        return mapToResponse(trade);
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> getAllTrades() {
        LOG.debug("Retrieving all trades");
        
        return tradeRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TradeResponse> getAllTrades(final Pageable pageable) {
        LOG.debug("Retrieving trades with pagination: {}", pageable);
        
        return tradeRepository.findAll(pageable)
            .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> getTradesByCounterparty(final String counterparty) {
        LOG.debug("Retrieving trades for counterparty: {}", counterparty);
        
        return tradeRepository.findByCounterparty(counterparty)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> getTradesByStatus(final TradeStatus status) {
        LOG.debug("Retrieving trades with status: {}", status);
        
        return tradeRepository.findByStatus(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public TradeResponse updateTradeStatus(final String tradeId, final TradeStatus newStatus) {
        LOG.info("Updating trade status for trade ID: {} to {}", tradeId, newStatus);
        
        var trade = tradeRepository.findByTradeId(tradeId)
            .orElseThrow(() -> new IllegalArgumentException("Trade not found with trade ID: " + tradeId));
        
        trade.setStatus(newStatus);
        trade.setUpdatedAt(LocalDateTime.now());
        
        var updatedTrade = tradeRepository.save(trade);
        LOG.info("Trade status updated successfully for trade ID: {}", tradeId);
        
        return mapToResponse(updatedTrade);
    }

    public void deleteTrade(final String tradeId) {
        LOG.info("Deleting trade with trade ID: {}", tradeId);
        
        var trade = tradeRepository.findByTradeId(tradeId)
            .orElseThrow(() -> new IllegalArgumentException("Trade not found with trade ID: " + tradeId));
        
        tradeRepository.delete(trade);
        LOG.info("Trade deleted successfully: {}", tradeId);
    }

    private Map<String, Object> enrichTrade(final TradeCreateRequest request) {
        var enrichmentRequest = new HashMap<String, Object>();
        enrichmentRequest.put("tradeId", request.getTradeId());
        enrichmentRequest.put("instrumentType", request.getInstrumentType());
        enrichmentRequest.put("underlyingAsset", request.getUnderlyingAsset());
        enrichmentRequest.put("notionalAmount", request.getNotionalAmount());
        enrichmentRequest.put("currency", request.getCurrency());
        enrichmentRequest.put("strikePrice", request.getStrikePrice());
        enrichmentRequest.put("maturityDate", request.getMaturityDate());
        enrichmentRequest.put("direction", request.getDirection().toString());

        return enrichmentClient.enrichTrade(enrichmentRequest);
    }

    private Trade mapToEntity(final TradeCreateRequest request) {
        return new Trade(
            request.getTradeId(),
            request.getCounterparty(),
            request.getInstrumentType(),
            request.getUnderlyingAsset(),
            request.getNotionalAmount(),
            request.getCurrency(),
            request.getStrikePrice(),
            request.getMaturityDate(),
            request.getDirection()
        );
    }

    private TradeResponse mapToResponse(final Trade trade) {
        return new TradeResponse(
            trade.getId(),
            trade.getTradeId(),
            trade.getCounterparty(),
            trade.getInstrumentType(),
            trade.getUnderlyingAsset(),
            trade.getNotionalAmount(),
            trade.getCurrency(),
            trade.getStrikePrice(),
            trade.getMaturityDate(),
            trade.getTradeDate(),
            trade.getDirection(),
            trade.getStatus(),
            trade.getEnrichmentData(),
            trade.getCreatedAt(),
            trade.getUpdatedAt()
        );
    }
}