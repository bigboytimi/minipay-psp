package com.minipay.api.reporting.controller;

import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.reporting.service.ReportGenerationService;
import com.minipay.api.settlement.enums.SettlementStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
@Tag(name = "Reports", description = "Reports operations API")
public class ReportingController {

    private final ReportGenerationService reportGenerationService;

    @GetMapping(value = REPORTS_TRANSACTIONS)
    @Operation(summary = "Generates Transactions Report", description = "Generates Transactions Report")
    public ResponseEntity<?> getTransactionReport(@RequestParam(required = false, defaultValue = "CSV") String format,
                                                  @RequestParam(required = false) LocalDateTime startDate,
                                                  @RequestParam(required = false) LocalDateTime endDate,
                                                  @RequestParam(required = false) String merchantId,
                                                  @RequestParam(required = false) PaymentChannel channel,
                                                  @RequestParam(required = false) PaymentStatus status) throws Exception {
        log.info("get transactions reports....");
        return reportGenerationService.getTransactionReport(format, startDate, endDate, channel, merchantId, status);
    }

    @GetMapping(value = REPORTS_SETTLEMENT)
    @Operation(summary = "Generates Settlement Report", description = "Generate Settlement Report")
    public ResponseEntity<?> getSettlementReport(@RequestParam(required = false, defaultValue = "CSV") String format,
                                                 @RequestParam(required = false) String batchReference,
                                                 @RequestParam(required = false) String merchantId,
                                                 @RequestParam(required = false) SettlementStatus status) throws Exception {
        log.info("get settlement reports...");
        return reportGenerationService.getSettlementReport(format, batchReference, merchantId, status);
    }
}
