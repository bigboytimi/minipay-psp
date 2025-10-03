package com.minipay.api.settlement.scheduler;

import com.minipay.api.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SettlementScheduler {
    private final SettlementService settlementService;

    // Runs every day at 12:00 AM
    @Scheduled(cron = "0 0 0 * * *")
    public void runDailySettlementJob() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        settlementService.generate(yesterday, yesterday);
    }
}
