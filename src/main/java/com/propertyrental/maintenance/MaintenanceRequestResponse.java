package com.propertyrental.maintenance;

import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceRequestResponse(
        Long id,
        Long unitId,
        String unitNumber,
        String description,
        Priority priority,
        Status status,
        Long createdById,
        String createdByUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Long> contractorIds
) {
}
