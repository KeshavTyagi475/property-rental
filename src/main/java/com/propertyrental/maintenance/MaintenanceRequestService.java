package com.propertyrental.maintenance;

import com.propertyrental.ResourceNotFoundException;
import com.propertyrental.unit.Unit;
import com.propertyrental.unit.UnitRepository;
import com.propertyrental.user.Role;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class MaintenanceRequestService {
	private final MaintenanceAssignmentRepository maintenanceAssignmentRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final MaintenanceTimelineRepository maintenanceTimelineRepository;

    public MaintenanceRequestService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            UnitRepository unitRepository,
            UserRepository userRepository,
            MaintenanceAssignmentRepository maintenanceAssignmentRepository,
            MaintenanceTimelineRepository maintenanceTimelineRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.maintenanceAssignmentRepository = maintenanceAssignmentRepository;
        this.maintenanceTimelineRepository = maintenanceTimelineRepository;
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

        //return maintenanceRequestRepository.save(maintenanceRequest);
        MaintenanceRequest savedRequest =
                maintenanceRequestRepository.save(maintenanceRequest);

        MaintenanceTimeline timeline = new MaintenanceTimeline();

        timeline.setMaintenanceRequest(savedRequest);
        timeline.setEventType(TimelineEventType.CREATED);
        timeline.setPerformedBy(createdBy);
        timeline.setCreatedAt(LocalDateTime.now());

        maintenanceTimelineRepository.save(timeline);

        return savedRequest;
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
    
    @Transactional
    public MaintenanceAssignment assignContractor(
            Long requestId,
            AssignContractorRequest request,
            String username) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found"));

        User performedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        User contractor = userRepository.findById(request.contractorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contractor not found"));

        if (contractor.getRole() != Role.MAINTENANCE_CONTRACTOR) {
            throw new IllegalArgumentException(
                    "User is not a maintenance contractor");
        }

        if (maintenanceAssignmentRepository
                .findByMaintenanceRequestIdAndContractorId(
                        requestId,
                        request.contractorId())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Contractor is already assigned to this request");
        }

        MaintenanceAssignment assignment = new MaintenanceAssignment();
        assignment.setMaintenanceRequest(maintenanceRequest);
        assignment.setContractor(contractor);
        assignment.setAssignedAt(LocalDateTime.now());

        MaintenanceAssignment savedAssignment =
                maintenanceAssignmentRepository.save(assignment);

        MaintenanceTimeline timeline = new MaintenanceTimeline();
        timeline.setMaintenanceRequest(maintenanceRequest);
        timeline.setEventType(TimelineEventType.ASSIGNED);
        timeline.setContractor(contractor);
        timeline.setPerformedBy(performedBy);
        timeline.setCreatedAt(LocalDateTime.now());

        maintenanceTimelineRepository.save(timeline);

        return savedAssignment;
    }
    
    @Transactional
    public void unassignContractor(
            Long requestId,
            Long contractorId,
            String username) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found"));

        User performedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        User contractor = userRepository.findById(contractorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contractor not found"));

        MaintenanceAssignment assignment =
                maintenanceAssignmentRepository
                        .findByMaintenanceRequestIdAndContractorId(
                                requestId,
                                contractorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contractor is not assigned to this request"));

        maintenanceAssignmentRepository.delete(assignment);

        MaintenanceTimeline timeline = new MaintenanceTimeline();
        timeline.setMaintenanceRequest(maintenanceRequest);
        timeline.setEventType(TimelineEventType.UNASSIGNED);
        timeline.setContractor(contractor);
        timeline.setPerformedBy(performedBy);
        timeline.setCreatedAt(LocalDateTime.now());

        maintenanceTimelineRepository.save(timeline);
    }
    
    @Transactional
    public MaintenanceRequest updateStatus(
            Long requestId,
            UpdateMaintenanceStatusRequest request,
            String username) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found"));

        User performedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Status oldStatus = maintenanceRequest.getStatus();
        Status newStatus = request.status();

        if (!isValidTransition(oldStatus, newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from "
                            + oldStatus + " to " + newStatus);
        }

        if (newStatus == Status.SCHEDULED &&
                maintenanceAssignmentRepository
                        .findByMaintenanceRequestId(requestId)
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "A contractor must be assigned before scheduling");
        }

        maintenanceRequest.setStatus(newStatus);
        maintenanceRequest.setUpdatedAt(LocalDateTime.now());

        MaintenanceRequest savedRequest =
                maintenanceRequestRepository.save(maintenanceRequest);

        MaintenanceTimeline timeline = new MaintenanceTimeline();

        timeline.setMaintenanceRequest(savedRequest);
        timeline.setEventType(TimelineEventType.STATUS_CHANGED);
        timeline.setOldStatus(oldStatus);
        timeline.setNewStatus(newStatus);
        timeline.setPerformedBy(performedBy);
        timeline.setCreatedAt(LocalDateTime.now());

        maintenanceTimelineRepository.save(timeline);

        return savedRequest;
    }

    private boolean isValidTransition(
            Status currentStatus,
            Status newStatus) {

        if (currentStatus == Status.REPORTED
                && newStatus == Status.TRIAGED) {
            return true;
        }

        if (currentStatus == Status.TRIAGED
                && newStatus == Status.SCHEDULED) {
            return true;
        }

        if (currentStatus == Status.SCHEDULED
                && newStatus == Status.RESOLVED) {
            return true;
        }

        if (currentStatus == Status.RESOLVED
                && newStatus == Status.TRIAGED) {
            return true;
        }

        return false;
    }
    
    public List<MaintenanceTimeline> getTimeline(Long requestId) {
        if (!maintenanceRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Maintenance request not found");
        }

        return maintenanceTimelineRepository
                .findByMaintenanceRequestIdOrderByCreatedAtAscIdAsc(requestId);
    }
    
    @Transactional
    public MaintenanceTimeline addNote(
            Long requestId,
            AddMaintenanceNoteRequest request,
            String username) {

        MaintenanceRequest maintenanceRequest =
                maintenanceRequestRepository.findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Maintenance request not found"));

        User performedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        MaintenanceTimeline timeline = new MaintenanceTimeline();

        timeline.setMaintenanceRequest(maintenanceRequest);
        timeline.setEventType(TimelineEventType.NOTE_ADDED);
        timeline.setNote(request.note());
        timeline.setPerformedBy(performedBy);
        timeline.setCreatedAt(LocalDateTime.now());

        return maintenanceTimelineRepository.save(timeline);
    }
    
    public List<MaintenanceRequest> getRequestsForContractor(String username) {

        User contractor = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (contractor.getRole() != Role.MAINTENANCE_CONTRACTOR) {
            throw new IllegalArgumentException(
                    "Only maintenance contractors can use this endpoint");
        }

        return maintenanceRequestRepository
                .findDistinctByAssignmentsContractorId(contractor.getId());
    }
    
    public Page<MaintenanceRequest> searchRequests(
            MaintenanceSearchRequest request) {

        int page = Math.max(request.page(), 0);
        int size = Math.min(Math.max(request.size(), 1), 100);

        Specification<MaintenanceRequest> specification =
                Specification.where(
                        MaintenanceRequestSpecifications.hasSearch(
                                request.search()))
                .and(
                        MaintenanceRequestSpecifications.hasUnitId(
                                request.unitId()))
                .and(
                        MaintenanceRequestSpecifications.hasStatus(
                                request.status()))
                .and(
                        MaintenanceRequestSpecifications.hasPriority(
                                request.priority()))
                .and(
                        MaintenanceRequestSpecifications.hasContractorId(
                                request.contractorId()));

        Sort sort = buildSort(
                request.sortBy(),
                request.sortDirection());

        Pageable pageable = PageRequest.of(page, size, sort);

        return maintenanceRequestRepository.findAll(
                specification,
                pageable);
    }
    
    private Sort buildSort(
            String sortBy,
            String sortDirection) {

        String property;

        if ("priority".equalsIgnoreCase(sortBy)) {
            property = "priority";
        } else if ("status".equalsIgnoreCase(sortBy)) {
            property = "status";
        } else {
            property = "createdAt";
        }

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(direction, property);
    }
}
