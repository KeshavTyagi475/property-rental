package com.propertyrental.dashboard;

import java.time.LocalDate;

public record WeeklyResolvedCount(
        LocalDate weekStart,
        long resolvedCount
) {
}
