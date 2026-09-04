package com.propertyrental.dashboard;

import com.propertyrental.maintenance.MaintenanceRequestRepository;
import com.propertyrental.rent.RentPaymentRepository;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final RentPaymentRepository rentPaymentRepository;

    public DashboardService(
            MaintenanceRequestRepository maintenanceRequestRepository,
            RentPaymentRepository rentPaymentRepository) {

        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.rentPaymentRepository = rentPaymentRepository;
    }
    
    public DashboardSummary getSummary() {

        LocalDate today = LocalDate.now();

        LocalDate monthStart =
                today.withDayOfMonth(1);

        LocalDateTime weekStart =
                today.with(DayOfWeek.MONDAY)
                        .atStartOfDay();

        LocalDateTime weekEnd =
                weekStart.plusDays(7);

        long openMaintenanceRequests =
                maintenanceRequestRepository
                        .countOpenMaintenanceRequests();

        long resolvedThisWeek =
                maintenanceRequestRepository
                        .countResolvedBetween(
                                weekStart,
                                weekEnd);

        BigDecimal totalRentCollectedThisMonth =
                rentPaymentRepository
                        .sumPaymentsForMonth(monthStart);

        long overdueRentUnits = 0;

        LocalDate gracePeriodEnd =
                monthStart.plusDays(3);

        if (!today.isBefore(gracePeriodEnd)) {
            overdueRentUnits =
                    rentPaymentRepository
                            .countUnitsWithoutFullPayment(
                                    monthStart);
        }

        return new DashboardSummary(
                openMaintenanceRequests,
                overdueRentUnits,
                resolvedThisWeek,
                totalRentCollectedThisMonth,
                getMaintenanceByStatus(),
                getMaintenanceByContractor(),
                getResolvedLastEightWeeks()
        );
    }
    
    private Map<String, Long> getMaintenanceByStatus() {

        return maintenanceRequestRepository
                .countMaintenanceByStatus()
                .stream()
                .collect(Collectors.toMap(
                        row -> ((Enum<?>) row[0]).name(),
                        row -> ((Number) row[1]).longValue()
                ));
    }
    
    private Map<String, Long> getMaintenanceByContractor() {

        return maintenanceRequestRepository
                .countMaintenanceByContractor()
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).longValue()
                ));
    }
    
    private List<WeeklyResolvedCount> getResolvedLastEightWeeks() {

        LocalDate today = LocalDate.now();

        LocalDate currentWeekStart =
                today.with(DayOfWeek.MONDAY);

        List<WeeklyResolvedCount> results =
                new ArrayList<>();

        for (int i = 7; i >= 0; i--) {

            LocalDate weekStart =
                    currentWeekStart.minusWeeks(i);

            LocalDateTime start =
                    weekStart.atStartOfDay();

            LocalDateTime end =
                    weekStart.plusWeeks(1).atStartOfDay();

            long count =
                    maintenanceRequestRepository
                            .countResolvedBetween(start, end);

            results.add(
                    new WeeklyResolvedCount(
                            weekStart,
                            count
                    )
            );
        }

        return results;
    }
    
}
