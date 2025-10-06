package com.mx.feenicia.memphis.commom.repository;

import com.mx.feenicia.memphis.commom.entity.TblHistorical;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TblHistoricalRepository  extends JpaRepository<TblHistorical, Long> {

    Optional<TblHistorical> findByTransactionIdAndTransactionTypeAndStatus(
            Long transactionId, String transactionType, String status);

    boolean existsByTransactionId(Long transactionId);

    boolean existsByRelatedTicketNumberAndStatusAndTransactionTypeIn(
            String relatedTicketNumber, String status, Collection<String> transactionTypes);

    long countByRelatedTicketNumberAndStatusAndTransactionTypeIn(
            String relatedTicketNumber, String status, Collection<String> transactionTypes);


}
