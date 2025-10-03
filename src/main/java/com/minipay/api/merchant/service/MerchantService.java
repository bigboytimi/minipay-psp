package com.minipay.api.merchant.service;

import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.dto.request.ChargeSettingRequest;
import com.minipay.api.merchant.dto.request.MerchantOnboardingRequest;
import com.minipay.api.merchant.dto.request.MerchantStatusUpdateRequest;
import com.minipay.api.merchant.dto.request.MerchantUpdateRequest;
import com.minipay.api.merchant.dto.response.MerchantResponse;
import com.minipay.api.merchant.dto.response.MerchantStatusUpdateResponse;
import com.minipay.common.ApiResponse;
import com.minipay.common.PageResponse;
import org.springframework.stereotype.Service;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Service
public interface MerchantService {
    MerchantResponse create(MerchantOnboardingRequest request);

    MerchantResponse update(String merchantId, MerchantUpdateRequest request);

    MerchantResponse fetch(String merchantId);


    Merchant getMerchantById(String merchantId);

    PageResponse<MerchantResponse> fetchAll(int pageNo, int pageSize, String name, String email, String settlementAccount, String settlementBank);

    MerchantResponse upsertSettings(String merchantId, ChargeSettingRequest request);

    MerchantResponse fetchSettings(String merchantId);

    MerchantStatusUpdateResponse statusUpdate(String id, MerchantStatusUpdateRequest statusUpdateRequest);
}
