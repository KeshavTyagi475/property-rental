package com.propertyrental.alert;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RentAlertScheduler {

    private final AlertService alertService;

    public RentAlertScheduler(AlertService alertService) {
        this.alertService = alertService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateRentOverdueAlerts() {
        alertService.generateRentOverdueAlerts();
    }
}
