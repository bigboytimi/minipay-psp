package com.minipay.api.settlement.controller;

import com.minipay.api.settlement.dto.response.SettlementBatchResponse;
import com.minipay.api.settlement.dto.response.SettlementDetailsResponse;
import com.minipay.api.settlement.enums.SettlementStatus;
import com.minipay.api.settlement.service.SettlementService;
import com.minipay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.minipay.api.ApiConstants.*;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RestController
@Tag(name = "Settlements", description = "Settlements operations API")
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping(value = SETTLEMENTS_GENERATE)
    @Operation(summary = "Generates Settlement", description = "Generate Settlement")
    public ResponseEntity<ApiResponse<SettlementBatchResponse>> generate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        log.info("generate settlement batches from {} to{}", from, to);
        SettlementBatchResponse response = settlementService.generate(from, to);
        return ApiResponse.success(response, "settlement batch generated");
    }

    @GetMapping(value = SETTLEMENTS)
    @Operation(summary = "Fetch Settlement Batch", description = "Fetch Settlement Batch")
    public ResponseEntity<ApiResponse<List<SettlementBatchResponse>>> batch(@RequestParam(required = false) String batchReference,
                                                                            @RequestParam(required = false) SettlementStatus status,
                                                                            @RequestParam(required = false) String merchantId) {
        log.info("fetch settlement batches...");
        List<SettlementBatchResponse> response = settlementService.batch(batchReference, status, merchantId);
        return ApiResponse.success(response, "retrieved successfully");
    }

    @GetMapping(value = SETTLEMENTS_WITH_SETTLEMENT_REF)
    @Operation(summary = "Fetch Settlement Single", description = "Fetch Settlement Single with details")
    public ResponseEntity<ApiResponse<SettlementDetailsResponse>> single(@PathVariable String settlementRef) {
        log.info("get settlement with settlementRef {}...", settlementRef);
        SettlementDetailsResponse response = settlementService.single(settlementRef);
        return ApiResponse.success(response, "settlement batch generated");
    }
}
