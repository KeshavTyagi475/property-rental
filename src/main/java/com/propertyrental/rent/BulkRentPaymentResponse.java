package com.propertyrental.rent;

import java.time.LocalDate;
import java.util.List;

public record BulkRentPaymentResponse(
        LocalDate paymentMonth,
        List<BulkRentPaymentResult> results
) {
}
