package com.propertyrental.maintenance;

public final class MaintenanceTimelineMapper {

    private MaintenanceTimelineMapper() {
    }

    public static MaintenanceTimelineResponse toResponse(
            MaintenanceTimeline timeline) {

        Long contractorId = timeline.getContractor() != null
                ? timeline.getContractor().getId()
                : null;

        String contractorUsername = timeline.getContractor() != null
                ? timeline.getContractor().getUsername()
                : null;

        return new MaintenanceTimelineResponse(
                timeline.getId(),
                timeline.getEventType(),
                timeline.getOldStatus(),
                timeline.getNewStatus(),
                timeline.getNote(),
                contractorId,
                contractorUsername,
                timeline.getPerformedBy().getId(),
                timeline.getPerformedBy().getUsername(),
                timeline.getCreatedAt()
        );
    }
}
