package com.propertyrental.maintenance;

import java.util.List;

public final class MaintenanceRequestMapper {

    private MaintenanceRequestMapper() {
    }

    public static MaintenanceRequestResponse toResponse(
            MaintenanceRequest request) {

        List<Long> contractorIds = request.getAssignments()
                .stream()
                .map(assignment -> assignment.getContractor().getId())
                .toList();

        return new MaintenanceRequestResponse(
                request.getId(),
                request.getUnit().getId(),
                request.getUnit().getUnitNumber(),
                request.getDescription(),
                request.getPriority(),
                request.getStatus(),
                request.getCreatedBy().getId(),
                request.getCreatedBy().getUsername(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                contractorIds
        );
    }
}
