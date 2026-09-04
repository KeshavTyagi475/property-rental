package com.propertyrental.rent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BulkRentPaymentRequest(
        @NotNull LocalDate paymentMonth,

        @NotEmpty
        @Valid
        List<BulkRentPaymentItem> payments
) {
}