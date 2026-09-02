package com.propertyrental.unit;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateUnitRequest(

        @NotBlank
        String unitNumber,

        @NotBlank
        String address,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal monthlyRent,

        String currentTenant
) {
}
