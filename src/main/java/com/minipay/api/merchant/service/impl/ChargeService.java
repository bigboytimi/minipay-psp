package com.minipay.api.merchant.service.impl;

import com.minipay.api.merchant.domain.ChargeSetting;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.repository.ChargeSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargeService {
    private final ChargeSettingRepository chargeSettingRepo;

    public ChargeSetting persist(ChargeSetting chargeSetting) {
        return chargeSettingRepo.save(chargeSetting);
    }

    public ChargeSetting getByMerchant(Merchant merchant) {
        return chargeSettingRepo.findByMerchant(merchant).orElse(null);
    }

}
