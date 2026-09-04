package com.propertyrental.maintenance;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping("/requests/{requestId}/assignments")
    public MaintenanceAssignment assignContractor(
            @PathVariable Long requestId,
            @Valid @RequestBody AssignContractorRequest request) {

        return maintenanceRequestService.assignContractor(
                requestId,
                request.contractorId()
        );
    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @DeleteMapping("/requests/{requestId}/assignments/{contractorId}")
    public void unassignContractor(
            @PathVariable Long requestId,
            @PathVariable Long contractorId) {

        maintenanceRequestService.unassignContractor(
                requestId,
                contractorId
        );
    }
    
    @PutMapping("/requests/{requestId}/status")
    public MaintenanceRequest updateStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceStatusRequest request) {

        return maintenanceRequestService.updateStatus(
                requestId,
                request.status()
        );
    }
}
