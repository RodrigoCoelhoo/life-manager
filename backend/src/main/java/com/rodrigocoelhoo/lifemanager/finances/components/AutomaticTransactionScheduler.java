package com.rodrigocoelhoo.lifemanager.finances.components;

import com.rodrigocoelhoo.lifemanager.finances.service.AutomaticTransactionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@Component
public class AutomaticTransactionScheduler {

    private final AutomaticTransactionService service;

    public AutomaticTransactionScheduler(AutomaticTransactionService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    @Transactional
    public void runDaily() {
        log.info("Starting daily automatic transactions for {}", LocalDate.now(ZoneOffset.UTC));
        service.processDailyAutomaticTransactions();
        log.info("Completed daily automatic transactions!");
    }
}

