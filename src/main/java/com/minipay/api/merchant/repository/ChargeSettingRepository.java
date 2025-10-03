package com.minipay.api.merchant.repository;

import com.minipay.api.merchant.domain.ChargeSetting;
import com.minipay.api.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChargeSettingRepository extends JpaRepository<ChargeSetting, String> {
    Optional<ChargeSetting> findByMerchant(Merchant merchant);

    Optional<ChargeSetting> findByMerchantId(String merchantId);
}
