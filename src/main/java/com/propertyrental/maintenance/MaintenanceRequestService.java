package com.propertyrental.maintenance;

import com.propertyrental.ResourceNotFoundException;
import com.propertyrental.unit.Unit;
import com.propertyrental.unit.UnitRepository;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MaintenanceRequestService {
	private final MaintenanceAssignmentRepository maintenanceAssignmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            UnitRepository unitRepository,
            UserRepository userRepository,
            MaintenanceAssignmentRepository maintenanceAssignmentRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.maintenanceAssignmentRepository = maintenanceAssignmentRepository;
    }

    public MaintenanceRequest createRequest(
            CreateMaintenanceRequest request,
            String username) {

        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Unit not found: " + request.unitId()));

        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username));

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();

        maintenanceRequest.setUnit(unit);
        maintenanceRequest.setDescription(request.description());
        maintenanceRequest.setPriority(request.priority());
        maintenanceRequest.setStatus(Status.REPORTED);
        maintenanceRequest.setCreatedBy(createdBy);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());
        maintenanceRequest.setUpdatedAt(LocalDateTime.now());

        return maintenanceRequestRepository.save(maintenanceRequest);
    }
    
    public MaintenanceRequest updateRequest(
            Long requestId,
            UpdateMaintenanceRequest request) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found: " + requestId));

        maintenanceRequest.setDescription(request.description());
        maintenanceRequest.setPriority(request.priority());
        maintenanceRequest.setUpdatedAt(LocalDateTime.now());

        return maintenanceRequestRepository.save(maintenanceRequest);
    }
    
    public MaintenanceAssignment assignContractor(
            Long requestId,
            Long contractorId) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found: " + requestId));

        User contractor = userRepository.findById(contractorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + contractorId));

        if (contractor.getRole() != com.propertyrental.user.Role.MAINTENANCE_CONTRACTOR) {
            throw new IllegalArgumentException(
                    "User must have the MAINTENANCE_CONTRACTOR role");
        }
        
        if (maintenanceAssignmentRepository
                .findByMaintenanceRequestIdAndContractorId(requestId, contractorId)
                .isPresent()) {
            throw new IllegalArgumentException(
                    "Contractor is already assigned to this maintenance request");
        }
        MaintenanceAssignment assignment = new MaintenanceAssignment();
        assignment.setMaintenanceRequest(maintenanceRequest);
        assignment.setContractor(contractor);
        assignment.setAssignedAt(LocalDateTime.now());

        return maintenanceAssignmentRepository.save(assignment);
    }
    
    @Transactional
    public void unassignContractor(
            Long requestId,
            Long contractorId) {

        if (!maintenanceRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException(
                    "Maintenance request not found: " + requestId);
        }

        if (!userRepository.existsById(contractorId)) {
            throw new ResourceNotFoundException(
                    "User not found: " + contractorId);
        }

        Optional<MaintenanceAssignment> assignment =
                maintenanceAssignmentRepository
                        .findByMaintenanceRequestIdAndContractorId(
                                requestId,
                                contractorId);

        if (assignment.isEmpty()) {
            throw new IllegalArgumentException(
                    "Contractor is not assigned to this maintenance request");
        }

        maintenanceAssignmentRepository
                .deleteByMaintenanceRequestIdAndContractorId(
                        requestId,
                        contractorId);
    }
}
