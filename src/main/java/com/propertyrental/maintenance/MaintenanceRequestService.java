package com.propertyrental.maintenance;

import com.propertyrental.ResourceNotFoundException;
import com.propertyrental.unit.Unit;
import com.propertyrental.unit.UnitRepository;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            UnitRepository unitRepository,
            UserRepository userRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
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
}
