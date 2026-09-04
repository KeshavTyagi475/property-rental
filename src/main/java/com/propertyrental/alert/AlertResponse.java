package com.propertyrental.alert;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlertResponse(
        Long id,
        Long unitId,
        String unitNumber,
        String message,
        LocalDate alertMonth,
        boolean dismissed,
        LocalDateTime createdAt
) {
}
