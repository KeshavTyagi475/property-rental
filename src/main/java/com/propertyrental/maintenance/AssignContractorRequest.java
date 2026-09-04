package com.propertyrental.maintenance;

import jakarta.validation.constraints.NotNull;

public record AssignContractorRequest(
        @NotNull
        Long contractorId
) {
}
