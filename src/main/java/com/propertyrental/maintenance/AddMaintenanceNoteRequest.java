package com.propertyrental.maintenance;

import jakarta.validation.constraints.NotBlank;

public record AddMaintenanceNoteRequest(
        @NotBlank String note
) {
}
