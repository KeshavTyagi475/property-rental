package com.propertyrental.rent;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BulkRentPaymentItem(
        @NotBlank String unitNumber,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}