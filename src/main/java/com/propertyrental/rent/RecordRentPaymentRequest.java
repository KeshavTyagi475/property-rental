package com.propertyrental.rent;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordRentPaymentRequest(
        @NotNull
        LocalDate paymentMonth,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}
