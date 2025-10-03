package com.minipay.api.settlement.service;

import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.dto.response.SettlementBatchResponse;
import com.minipay.api.settlement.dto.response.SettlementDetailsResponse;
import com.minipay.api.settlement.enums.SettlementStatus;
import com.minipay.common.ApiResponse;

import java.time.LocalDate;
import java.util.List;

public interface SettlementService {
    SettlementBatchResponse generate(LocalDate from, LocalDate to);
    List<SettlementBatchResponse> batch(String batchReference, SettlementStatus status, String merchantId);
    SettlementDetailsResponse single(String settlementRef);

    List<SettlementBatch> getSettlementBatch(String batchReference, String merchantId, SettlementStatus status);
}
