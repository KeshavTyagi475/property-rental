package com.propertyrental.maintenance;

public record MaintenanceSearchRequest(
        String search,
        Long unitId,
        Status status,
        Long contractorId,
        Priority priority,
        String sortBy,
        String sortDirection,
        int page,
        int size
) {
}
