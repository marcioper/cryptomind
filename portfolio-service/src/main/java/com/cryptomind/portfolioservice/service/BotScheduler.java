package com.cryptomind.portfolioservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BotScheduler {

    private final BotService botService;

    @Value("${bot.schedule.ms:300000}")
    private long scheduleMs;

    public BotScheduler(BotService botService) {
        this.botService = botService;
    }

    @Scheduled(fixedDelayString = "${bot.schedule.ms:300000}")
    public void runBotTick() {
        botService.executeTick();
    }
}
