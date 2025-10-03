package com.minipay.api.merchant.controller;

import com.minipay.api.merchant.dto.request.ChargeSettingRequest;
import com.minipay.api.merchant.dto.request.MerchantOnboardingRequest;
import com.minipay.api.merchant.dto.request.MerchantStatusUpdateRequest;
import com.minipay.api.merchant.dto.request.MerchantUpdateRequest;
import com.minipay.api.merchant.dto.response.MerchantResponse;
import com.minipay.api.merchant.dto.response.MerchantStatusUpdateResponse;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.common.ApiResponse;
import com.minipay.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "Merchants", description = "Merchants operations API")
public class MerchantOnboardingController {
    private final MerchantService merchantService;

    @PostMapping(value = MERCHANTS)
    @PreAuthorize("hasRole('MAKER')")
    @Operation(summary = "Create Merchant", description = "Creates a new merchant")
    public ResponseEntity<ApiResponse<MerchantResponse>> create(@RequestBody @Valid MerchantOnboardingRequest request) {
        log.info("initiating merchant onboarding...");
        MerchantResponse response = merchantService.create(request);
        return ApiResponse.success(response, "merchant created.");
    }

    @PostMapping(value = MERCHANTS_APPROVAL)
    @PreAuthorize("hasRole('CHECKER')")
    @Operation(summary = "Update Merchant Status", description = "Updates merchant status")
    public ResponseEntity<ApiResponse<MerchantStatusUpdateResponse>> statusUpdate(@PathVariable String id, @RequestBody @Valid MerchantStatusUpdateRequest statusUpdateRequest) {
        log.info("approving merchant status update...");
        MerchantStatusUpdateResponse statusUpdateResponse = merchantService.statusUpdate(id, statusUpdateRequest);
        return ApiResponse.success(statusUpdateResponse, "merchant status updated");
    }

    @PutMapping(value = MERCHANTS_WITH_ID)
    @Operation(summary = "Update Merchant", description = "Updates merchant details")
    @PreAuthorize("hasRole('MAKER')")
    public ResponseEntity<ApiResponse<MerchantResponse>> update(@PathVariable String merchantId, @RequestBody @Valid MerchantUpdateRequest request) {
        log.info("initiating merchant update...");
        MerchantResponse response = merchantService.update(merchantId, request);
        return ApiResponse.success(response, "merchant updated");
    }

    @GetMapping(value = MERCHANTS_WITH_ID)
    @Operation(summary = "Fetch Merchant", description = "Fetch merchant details")
    public ResponseEntity<ApiResponse<MerchantResponse>> fetch(@PathVariable String merchantId) {
        log.info("fetching merchant with ID {}...", merchantId);
        MerchantResponse response = merchantService.fetch(merchantId);
        return ApiResponse.success(response, "merchant fetched successfully");
    }

    @GetMapping(value = MERCHANTS)
    @Operation(summary = "Fetch Merchant", description = "Fetch merchant details")
    public ResponseEntity<?> fetchAll(@RequestParam(required = false, defaultValue = "0") int pageNo,
                                                                @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String email,
                                                                @RequestParam(required = false) String settlementAccount,
                                                                @RequestParam(required = false) String settlementBank) {
        log.info("fetching all merchants...");
        PageResponse<MerchantResponse> response = merchantService.fetchAll(pageNo, pageSize, name, email, settlementAccount, settlementBank);
        return ApiResponse.success(response, "merchant fetched successfully");
    }

    @PutMapping(value = MERCHANT_CHARGE_SETTINGS)
    @PreAuthorize("hasRole('MAKER')")
    @Operation(summary = "Update Merchant Configuration", description = "Update Merchant Configuration")
    public ResponseEntity<ApiResponse<MerchantResponse>> upsertSettings(@PathVariable String merchantId, @RequestBody @Valid ChargeSettingRequest request) {
        log.info("upsert charge settings for merchant with id: {}...", merchantId);
        MerchantResponse response = merchantService.upsertSettings(merchantId, request);
        return ApiResponse.success(response, "charges configuration");
    }

    @GetMapping(value = MERCHANT_CHARGE_SETTINGS)
    @Operation(summary = "Fetch Merchant Configuration", description = "Fetch Merchant Configuration")
    public ResponseEntity<ApiResponse<MerchantResponse>> fetchSettings(@PathVariable String merchantId) {
        log.info("fetching charge settings for merchant with id: {}...", merchantId);
        MerchantResponse response =  merchantService.fetchSettings(merchantId);
        return ApiResponse.success(response, "charge settings fetched successfully");
    }

}
