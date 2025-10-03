package com.minipay.api.settlement.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SettlementDetailsResponse {
    private SettlementBatchResponse batch;
    private List<SettlementItemResponse> items;
}
