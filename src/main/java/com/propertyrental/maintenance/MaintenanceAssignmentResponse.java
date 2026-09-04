package com.propertyrental.maintenance;

import java.time.LocalDateTime;

public record MaintenanceAssignmentResponse(
        Long id,
        Long contractorId,
        String contractorUsername,
        LocalDateTime assignedAt
) {
}