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
    public MaintenanceRequestResponse createRequest(
            @Valid @RequestBody CreateMaintenanceRequest request,
            Authentication authentication) {

    	return MaintenanceRequestMapper.toResponse(
    	        maintenanceRequestService.createRequest(
    	                request,
    	                authentication.getName())
    	);
    }
    
    @PutMapping("/{requestId}")
    public MaintenanceRequestResponse updateRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceRequest request,
            Authentication authentication) {
        return MaintenanceRequestMapper.toResponse(
                maintenanceRequestService.updateRequest(
                        requestId,
                        request,
                        authentication.getName()));
    }
    
    @PostMapping("/requests/{requestId}/assignments")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public MaintenanceAssignmentResponse assignContractor(
            @PathVariable Long requestId,
            @Valid @RequestBody AssignContractorRequest request,
            Authentication authentication) {

        return MaintenanceAssignmentMapper.toResponse(
                maintenanceRequestService.assignContractor(
                        requestId,
                        request,
                        authentication.getName()));
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
    public MaintenanceRequestResponse updateStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateMaintenanceStatusRequest request,
            Authentication authentication) {
        return MaintenanceRequestMapper.toResponse(
                maintenanceRequestService.updateStatus(
                        requestId,
                        request,
                        authentication.getName()));
    }
    
    @GetMapping("/{requestId}/timeline")
    public List<MaintenanceTimelineResponse> getTimeline(
            @PathVariable Long requestId,
            Authentication authentication) {

        return maintenanceRequestService.getTimeline(
                requestId,
                authentication.getName())
                .stream()
                .map(MaintenanceTimelineMapper::toResponse)
                .toList();
    }
    
    @GetMapping("/requests/{requestId}")
    public MaintenanceRequestResponse getRequestById(
            @PathVariable Long requestId,
            Authentication authentication) {

        return MaintenanceRequestMapper.toResponse(
                maintenanceRequestService.getRequestById(
                        requestId,
                        authentication.getName()));
    }
    
    @PostMapping("/requests/{requestId}/timeline/notes")
    public MaintenanceTimelineResponse addNote(
            @PathVariable Long requestId,
            @Valid @RequestBody AddMaintenanceNoteRequest request,
            Authentication authentication) {

        return MaintenanceTimelineMapper.toResponse(
                maintenanceRequestService.addNote(
                        requestId,
                        request,
                        authentication.getName()));
    }
    
    @GetMapping("/contractor/requests")
    @PreAuthorize("hasRole('MAINTENANCE_CONTRACTOR')")
    public List<MaintenanceRequestResponse> getContractorRequests(
            Authentication authentication) {
        return maintenanceRequestService.getRequestsForContractor(
                authentication.getName())
                .stream()
                .map(MaintenanceRequestMapper::toResponse)
                .toList();
    }
    
    @GetMapping("/requests/search")
    public Page<MaintenanceRequestResponse> searchRequests(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long contractorId,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

    	MaintenanceSearchRequest request = new MaintenanceSearchRequest(
    	        text,
    	        unitId,
    	        status,
    	        contractorId,
    	        priority,
    	        sortBy,
    	        direction,
    	        page,
    	        size);

        return maintenanceRequestService.searchRequests(
                request,
                authentication.getName())
                .map(MaintenanceRequestMapper::toResponse);
    }
}
