package com.propertyrental.maintenance;

import jakarta.validation.constraints.NotNull;

public record UpdateMaintenanceStatusRequest(
        @NotNull
        Status status
) {
}
