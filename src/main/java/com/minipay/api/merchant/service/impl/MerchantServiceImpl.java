package com.minipay.api.merchant.service.impl;

import com.minipay.api.authentication.domain.enums.UserStatus;
import com.minipay.api.merchant.domain.ChargeSetting;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.dto.request.ChargeSettingRequest;
import com.minipay.api.merchant.dto.request.MerchantOnboardingRequest;
import com.minipay.api.merchant.dto.request.MerchantStatusUpdateRequest;
import com.minipay.api.merchant.dto.request.MerchantUpdateRequest;
import com.minipay.api.merchant.dto.response.ChargeSettingResponse;
import com.minipay.api.merchant.dto.response.MerchantResponse;
import com.minipay.api.merchant.dto.response.MerchantStatusUpdateResponse;
import com.minipay.api.merchant.repository.MerchantRepository;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.common.ApiResponse;
import com.minipay.common.PageResponse;
import com.minipay.common.ResponseCode;
import com.minipay.exception.ApiException;
import com.minipay.utils.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final ChargeService chargeService;
    @Override
    @Transactional
    public MerchantResponse create(MerchantOnboardingRequest request) {
        log.info("onboarding merchant...");

        if (merchantRepository.existsByEmail(request.getMerchantEmail())){
            log.info("email already exists....");
            throw new ApiException("merchant email already exists");
        }

        try {
            String webhookSecret = RandomGenerator.generateWebhookSecret();

            Merchant merchant = new Merchant();
            merchant.setName(request.getMerchantName());
            merchant.setEmail(request.getMerchantEmail());
            merchant.setMerchantId(generateMerchantId());
            merchant.setStatus(UserStatus.INACTIVE);
            merchant.setCreatedAt(LocalDateTime.now());
            merchant.setMerchantId(RandomGenerator.generateMerchantIdNumber());
            merchant.setSettlementBank(request.getSettlementBank());
            merchant.setSettlementAccount(request.getSettlementAccount());
            merchant.setCallBackUrl(request.getCallbackUrl());
            merchant.setWebhookSecret(webhookSecret);

            Merchant savedMerchant = merchantRepository.save(merchant);

            ChargeSetting chargeSetting = setMerchantChangeConfiguration(request, savedMerchant);

            ChargeSetting savedConfiguration = chargeService.persist(chargeSetting);

            ChargeSettingResponse settingResponse = mapToResponse(savedConfiguration);

            return buildMerchantResponse(savedMerchant, settingResponse);
        }catch (Exception e){
            log.info("error occurred", e);
            throw new ApiException("Error Occurred", ResponseCode.SYSTEM_MALFUNCTION.getCode());
        }
    }

    private String generateMerchantId() {
        return "MER-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    @Override
    public MerchantResponse update(String merchantId, MerchantUpdateRequest request) {
        log.info("updating merchant details...");

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ApiException("merchant not found"));

        boolean updated = false;

        try{
            if (StringUtils.hasText(request.getMerchantEmail())){
                merchant.setEmail(request.getMerchantEmail());
                updated = true;
            }

            if (StringUtils.hasText(request.getMerchantName())){
                merchant.setName(request.getMerchantName());
                updated = true;
            }

            if (StringUtils.hasText(request.getSettlementBank())){
                merchant.setSettlementBank(request.getSettlementBank());
                updated = true;
            }

            if (StringUtils.hasText(request.getSettlementAccount())){
                merchant.setSettlementAccount(request.getSettlementAccount());
                updated = true;
            }

            if (StringUtils.hasText(request.getCallbackUrl())){
                merchant.setCallBackUrl(request.getCallbackUrl());
                updated = true;
            }

            if (!updated){
                throw new ApiException("Nothing to update");
            }


            //inactive until update is approved or denied by checker
            merchant.setStatus(UserStatus.INACTIVE);
            Merchant updatedMerchant = merchantRepository.save(merchant);

            return buildMerchantResponse(updatedMerchant, null);

        }catch (Exception e){
            log.info("error occurred", e);
            throw new ApiException("Error Occurred", ResponseCode.SYSTEM_MALFUNCTION.getCode());
        }
    }


    @Override
    public MerchantStatusUpdateResponse statusUpdate(String id, MerchantStatusUpdateRequest statusUpdateRequest) {
        Merchant merchant = merchantRepository.findById(id).orElseThrow(()-> new ApiException("merchant not found"));

        UserStatus newStatus = statusUpdateRequest.getStatus();
        UserStatus currentStatus = merchant.getStatus();

        if (currentStatus == newStatus) {
            throw new ApiException("Merchant is already in status: " + newStatus);
        }

        if (newStatus == UserStatus.ACTIVE) {
            if (currentStatus == UserStatus.INACTIVE || currentStatus == UserStatus.SUSPENDED) {
                merchant.setStatus(UserStatus.ACTIVE);
            } else {
                throw new ApiException("Merchant cannot be approved from current state: " + currentStatus);
            }
        }

        if (newStatus == UserStatus.SUSPENDED || newStatus == UserStatus.INACTIVE) {
            merchant.setStatus(newStatus);
        }

        merchant.setUpdatedAt(LocalDateTime.now());
        merchantRepository.save(merchant);

        return MerchantStatusUpdateResponse.builder()
                .merchantId(merchant.getMerchantId())
                .status(merchant.getStatus().name())
                .build();
    }


    @Override
    public MerchantResponse fetch(String merchantId) {

        log.info("fetching merchant details...");
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(()-> new ApiException("merchant not found"));
        return buildMerchantResponse(merchant, null);
    }

    @Override
    public Merchant getMerchantById(String merchantId) {
        return merchantRepository.findById(merchantId).orElseThrow(()-> new ApiException("merchant not found"));
    }

    @Override
    public MerchantResponse fetchSettings(String merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ApiException("Merchant not found"));

        ChargeSetting setting = chargeService.getByMerchant(merchant);

        ChargeSettingResponse settingResponse = mapToResponse(setting);

        return buildMerchantResponse(merchant, settingResponse);
    }

    @Override
    public PageResponse<MerchantResponse> fetchAll(int pageNo, int pageSize, String name, String email, String settlementAccount, String settlementBank) {
        PageRequest pageable = PageRequest.of(pageNo, pageSize);

        Specification<Merchant> merchantSpecification = buildSpecification(name, email, settlementAccount, settlementBank);

        Page<Merchant> merchantPage = merchantRepository.findAll(merchantSpecification, pageable);

        if (merchantPage.isEmpty()) {
            return new PageResponse<>(
                    Collections.emptyList(),
                    merchantPage.getNumber(),
                    merchantPage.getSize(),
                    merchantPage.getTotalElements(),
                    merchantPage.getTotalPages(),
                    merchantPage.isLast()
            );
        }

        List<MerchantResponse> responses = merchantPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                merchantPage.getNumber(),
                merchantPage.getSize(),
                merchantPage.getTotalElements(),
                merchantPage.getTotalPages(),
                merchantPage.isLast()
        );
    }

    @Override
    public MerchantResponse upsertSettings(String merchantId, ChargeSettingRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ApiException("Merchant not found"));

        ChargeSetting setting = chargeService.getByMerchant(merchant);

        if (setting == null){
            throw new ApiException("Merchant has no existing charge setting to update");
        }

        boolean updated = false;

        if (request.getFixedFee() != null){
            setting.setFixedFee(request.getFixedFee());
            updated = true;
        }

        if (request.getUseFixedMSC() != null){
            setting.setUseFixedMSC(request.getUseFixedMSC());
            updated = true;
        }

        if (request.getMscCap() != null){
            setting.setMscCap(request.getMscCap());
            updated = true;

        }

        if (request.getPercentageFee() != null){
            setting.setPercentageFee(request.getPercentageFee());
            updated = true;

        }
        if (request.getPlatformProviderCap() != null){
            setting.setPlatformProviderCap(request.getPlatformProviderCap());
            updated = true;

        }

        if (request.getPlatformProviderRate() != null){
            setting.setPlatformProviderRate(request.getPlatformProviderRate());
            updated = true;
        }

        if (request.getVatRate() != null){
            setting.setVatRate(request.getVatRate());
            updated = true;
        }

        if (!updated){
            throw new ApiException("Nothing to update");
        }

        try {
            ChargeSetting savedSetting = chargeService.persist(setting);

            ChargeSettingResponse settingResponse = mapToResponse(savedSetting);

            return buildMerchantResponse(merchant, settingResponse);
        }catch (Exception e){
            log.info("error occurred", e);
            throw new ApiException("Error Occurred", ResponseCode.SYSTEM_MALFUNCTION.getCode());
        }
    }

    private Specification<Merchant> buildSpecification(String name, String email, String settlementAccount, String settlementBank) {
        return null;
    }


    private ChargeSetting setMerchantChangeConfiguration(MerchantOnboardingRequest request, Merchant savedMerchant) {
        ChargeSettingRequest chargeSettingRequest =  request.getChargeSettingRequest();

        ChargeSetting chargeSetting = new ChargeSetting();
        chargeSetting.setMerchant(savedMerchant);
        chargeSetting.setFixedFee(chargeSettingRequest.getFixedFee());
        chargeSetting.setPercentageFee(chargeSettingRequest.getPercentageFee());
        chargeSetting.setMscCap(chargeSettingRequest.getMscCap());
        chargeSetting.setPlatformProviderCap(chargeSettingRequest.getPlatformProviderCap());
        chargeSetting.setPlatformProviderRate(chargeSettingRequest.getPlatformProviderRate());
        chargeSetting.setUseFixedMSC(chargeSettingRequest.getUseFixedMSC());
        return chargeSetting;
    }

    private ChargeSettingResponse mapToResponse(ChargeSetting savedConfiguration) {
        ChargeSettingResponse response = new ChargeSettingResponse();
        BeanUtils.copyProperties(savedConfiguration, response);
        return response;
    }

    private MerchantResponse mapToResponse(Merchant merchant) {
        MerchantResponse response = new MerchantResponse();

        response.setMerchantId(merchant.getMerchantId());
        response.setMerchantName(merchant.getName());
        response.setMerchantEmail(merchant.getEmail());
        response.setStatus(merchant.getStatus().name());
        response.setSettlementBank(merchant.getSettlementBank());
        response.setSettlementAccount(merchant.getSettlementAccount());
        response.setWebhookSecret(merchant.getWebhookSecret());
        response.setCallbackUrl(merchant.getCallBackUrl());

        ChargeSetting setting = chargeService.getByMerchant(merchant);

        if (setting == null){
            return response;
        }

        ChargeSettingResponse chargeSettingResponse = mapToResponse(setting);
        response.setChargeSettingResponse(chargeSettingResponse);
        return response;
    }


    private MerchantResponse buildMerchantResponse(Merchant savedMerchant, ChargeSettingResponse chargeSetting) {
        MerchantResponse response = new MerchantResponse();
        response.setMerchantName(savedMerchant.getName());
        response.setMerchantEmail(savedMerchant.getEmail());
        response.setMerchantId(savedMerchant.getMerchantId());
        response.setStatus(savedMerchant.getStatus().name());
        response.setSettlementAccount(savedMerchant.getSettlementAccount());
        response.setSettlementBank(savedMerchant.getSettlementBank());
        response.setWebhookSecret(savedMerchant.getWebhookSecret());
        response.setCallbackUrl(savedMerchant.getCallBackUrl());

        if (chargeSetting != null){
            response.setChargeSettingResponse(chargeSetting);
        }
        return response;
    }
}
