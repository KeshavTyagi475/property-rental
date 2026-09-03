package com.propertyrental.maintenance;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    public MaintenanceRequestController(
            MaintenanceRequestService maintenanceRequestService) {
        this.maintenanceRequestService = maintenanceRequestService;
    }

    @PostMapping("/requests")
    public MaintenanceRequest createRequest(
            @Valid @RequestBody CreateMaintenanceRequest request,
            Authentication authentication) {

        return maintenanceRequestService.createRequest(
                request,
                authentication.getName()
        );
    }
    
    @PutMapping("/requests/{requestId}")
    public MaintenanceRequest updateRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceRequest request) {

        return maintenanceRequestService.updateRequest(
                requestId,
                request
        );
    }
}
