package com.jwattsuk.trade.repository;

import com.jwattsuk.trade.entity.Trade;
import com.jwattsuk.trade.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<Trade> findByTradeId(String tradeId);

    List<Trade> findByCounterparty(String counterparty);

    List<Trade> findByStatus(TradeStatus status);

    List<Trade> findByInstrumentType(String instrumentType);

    @Query("SELECT t FROM Trade t WHERE t.maturityDate BETWEEN :startDate AND :endDate")
    List<Trade> findByMaturityDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM Trade t WHERE t.counterparty = :counterparty AND t.status = :status")
    List<Trade> findByCounterpartyAndStatus(@Param("counterparty") String counterparty, @Param("status") TradeStatus status);

    boolean existsByTradeId(String tradeId);
}