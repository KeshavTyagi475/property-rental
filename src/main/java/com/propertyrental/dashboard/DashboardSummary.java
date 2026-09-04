package com.propertyrental.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummary(
        long openMaintenanceRequests,
        long overdueRentUnits,
        long resolvedThisWeek,
        BigDecimal totalRentCollectedThisMonth,
        Map<String, Long> maintenanceByStatus,
        Map<String, Long> maintenanceByContractor,
        List<WeeklyResolvedCount> resolvedLastEightWeeks
) {
}
