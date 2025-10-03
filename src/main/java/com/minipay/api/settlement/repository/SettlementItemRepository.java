package com.minipay.api.settlement.repository;

import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.domains.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementItemRepository extends JpaRepository<SettlementItem, String> {
    List<SettlementItem> findByBatch(SettlementBatch batch);
}
