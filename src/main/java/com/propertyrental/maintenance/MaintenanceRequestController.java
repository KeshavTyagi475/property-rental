package com.propertyrental.maintenance;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
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
    
    @PutMapping("/{requestId}")
    public MaintenanceRequest updateRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceRequest request,
            Authentication authentication) {

        return maintenanceRequestService.updateRequest(
                requestId,
                request,
                authentication.getName());
    }
    
    @PostMapping("/requests/{requestId}/assignments")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public MaintenanceAssignment assignContractor(
            @PathVariable Long requestId,
            @Valid @RequestBody AssignContractorRequest request,
            Authentication authentication) {

        return maintenanceRequestService.assignContractor(
                requestId,
                request,
                authentication.getName());
    }
    
    @DeleteMapping("/requests/{requestId}/assignments/{contractorId}")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Void> unassignContractor(
            @PathVariable Long requestId,
            @PathVariable Long contractorId,
            Authentication authentication) {

        maintenanceRequestService.unassignContractor(
                requestId,
                contractorId,
                authentication.getName());

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/requests/{requestId}/status")
    public MaintenanceRequest updateStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceStatusRequest request,
            Authentication authentication) {

        return maintenanceRequestService.updateStatus(
                requestId,
                request,
                authentication.getName());
    }
    
    @GetMapping("/{requestId}/timeline")
    public List<MaintenanceTimeline> getTimeline(
            @PathVariable Long requestId,
            Authentication authentication) {

        return maintenanceRequestService.getTimeline(
                requestId,
                authentication.getName());
    }
    
    @PostMapping("/requests/{requestId}/timeline/notes")
    public MaintenanceTimeline addNote(
            @PathVariable Long requestId,
            @Valid @RequestBody AddMaintenanceNoteRequest request,
            Authentication authentication) {

        return maintenanceRequestService.addNote(
                requestId,
                request,
                authentication.getName());
    }
    
    @GetMapping("/contractor/requests")
    @PreAuthorize("hasRole('MAINTENANCE_CONTRACTOR')")
    public List<MaintenanceRequest> getContractorRequests(
            Authentication authentication) {

        return maintenanceRequestService
                .getRequestsForContractor(authentication.getName());
    }
    
    @GetMapping("/requests/search")
    public Page<MaintenanceRequest> searchRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long contractorId,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        MaintenanceSearchRequest request =
                new MaintenanceSearchRequest(
                        search,
                        unitId,
                        status,
                        contractorId,
                        priority,
                        sortBy,
                        sortDirection,
                        page,
                        size);

        return maintenanceRequestService.searchRequests(
                request,
                authentication.getName());
    }
}
