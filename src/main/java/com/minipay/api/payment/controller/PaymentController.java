package com.minipay.api.payment.controller;

import com.minipay.api.payment.dto.request.ProcessPaymentRequest;
import com.minipay.api.payment.dto.request.InitiatePaymentRequest;
import com.minipay.api.payment.dto.response.PaymentResponse;
import com.minipay.api.payment.service.PaymentService;
import com.minipay.common.ApiResponse;
import com.minipay.common.PageResponse;
import com.minipay.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
@Tag(name = "Payments", description = "Payments operations API")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(value = PAYMENTS)
    @Operation(summary = "Initiate Payment", description = "Initiate Payment")
    @PreAuthorize("hasRole('MERCHANT_USER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> payment(@RequestBody @Valid InitiatePaymentRequest request) {
        log.info("initiating payment...");
        PaymentResponse response = paymentService.payment(request);
        return ApiResponse.success(response, "payment initiated successfully");
    }

    @GetMapping(value = PAYMENT_WITH_REFERENCE)
    @Operation(summary = "Fetch Payment", description = "Fetch Payment details By reference")
    @PreAuthorize("hasRole('MERCHANT_USER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> fetch(@PathVariable String paymentRef) {
        log.info("fetching payment with reference {}...", paymentRef);
        PaymentResponse response = paymentService.fetch(paymentRef);
        return ApiResponse.success(response, "payment retrieved successfully");
    }

    @PostMapping(value = PAYMENTS_APPROVE)
    @Operation(summary = "Approve Payment", description = "Approve Payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> approve(@PathVariable String paymentRef, @RequestParam boolean success) {
        log.info("initiating payment status update...");
        PaymentResponse response = paymentService.approvePayment(paymentRef, success);
        return ApiResponse.success(response, "payment status updated successfully");
    }


    @GetMapping(value = PAYMENTS)
    @Operation(summary = "Fetch Payment", description = "Fetch Payment details by filters")
    @PreAuthorize("hasRole('MERCHANT_USER')")
    public ResponseEntity<?> filter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                @RequestParam(required = false) String merchantId,
                                                                @RequestParam(required = false) String channel,
                                                                @RequestParam(required = false) String status) {
        log.info("filtering payment...");

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ApiException("startDate must be before endDate");
        }

        List<PaymentResponse> response = paymentService.filter(startDate, endDate, merchantId, channel, status);
        return ApiResponse.success(response, "payment retrieved successfully");
    }

    @PostMapping(value = PROCESSOR_CALLBACK)
    @Operation(summary = "Process Payment", description = "Process payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@RequestBody @Valid ProcessPaymentRequest request) {
        log.info("simulating processor callback...");
        PaymentResponse response = paymentService.processPayment(request);
        return ApiResponse.success(response, "callback confirmed");
    }

}
