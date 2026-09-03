package com.propertyrental.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMaintenanceRequest(
        @NotNull
        Long unitId,

        @NotBlank
        String description,

        @NotNull
        Priority priority
) {
}
