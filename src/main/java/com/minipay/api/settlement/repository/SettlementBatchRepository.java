package com.minipay.api.settlement.repository;

import com.minipay.api.settlement.domains.SettlementBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettlementBatchRepository extends JpaRepository<SettlementBatch, String>, JpaSpecificationExecutor<SettlementBatch> {
    Optional<SettlementBatch> findBySettlementReference(String settlementRef);
}
