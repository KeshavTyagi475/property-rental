package com.propertyrental.maintenance;

import java.time.LocalDateTime;

public record MaintenanceTimelineResponse(
        Long id,
        TimelineEventType eventType,
        Status oldStatus,
        Status newStatus,
        String note,
        Long contractorId,
        String contractorUsername,
        Long performedById,
        String performedByUsername,
        LocalDateTime createdAt
) {
}
