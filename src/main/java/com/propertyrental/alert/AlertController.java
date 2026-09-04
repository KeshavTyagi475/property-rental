package com.propertyrental.alert;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public List<AlertResponse> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }

    @DeleteMapping("/{alertId}")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public void dismissAlert(@PathVariable Long alertId) {
        alertService.dismissAlert(alertId);
    }
}