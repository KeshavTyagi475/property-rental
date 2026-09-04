package com.propertyrental.rent;

import java.math.BigDecimal;

public record BulkRentPaymentResult(
        String unitNumber,
        BigDecimal monthlyRent,
        BigDecimal paymentAmount,
        String tenant,
        String status
) {
}
