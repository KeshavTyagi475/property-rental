package com.propertyrental.maintenance;

public final class MaintenanceAssignmentMapper {

    private MaintenanceAssignmentMapper() {
    }

    public static MaintenanceAssignmentResponse toResponse(
            MaintenanceAssignment assignment) {

        return new MaintenanceAssignmentResponse(
                assignment.getId(),
                assignment.getContractor().getId(),
                assignment.getContractor().getUsername(),
                assignment.getAssignedAt()
        );
    }
}