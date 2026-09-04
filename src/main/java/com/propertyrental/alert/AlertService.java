package com.propertyrental.alert;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertyrental.rent.RentPaymentRepository;
import com.propertyrental.unit.Unit;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final RentPaymentRepository rentPaymentRepository;

    public AlertService(
            AlertRepository alertRepository,
            RentPaymentRepository rentPaymentRepository) {

        this.alertRepository = alertRepository;
        this.rentPaymentRepository = rentPaymentRepository;
    }

    @Transactional
    public void createRentOverdueAlert(Unit unit, LocalDate paymentMonth) {

        boolean alreadyExists = alertRepository
                .findByUnitIdAndAlertTypeAndAlertMonth(
                        unit.getId(),
                        AlertType.RENT_OVERDUE,
                        paymentMonth)
                .isPresent();

        if (alreadyExists) {
            return;
        }

        Alert alert = new Alert();
        alert.setCreatedAt(java.time.LocalDateTime.now());
        alert.setUnit(unit);
        alert.setAlertType(AlertType.RENT_OVERDUE);
        alert.setAlertMonth(paymentMonth);
        alert.setDismissedAt(null);
        alert.setDismissedBy(null);
        alert.setMessage(
                "Rent is overdue for unit "
                        + unit.getUnitNumber()
                        + " for "
                        + paymentMonth);

        alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findByDismissedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(alert -> new AlertResponse(
                        alert.getId(),
                        alert.getUnit().getId(),
                        alert.getUnit().getUnitNumber(),
                        alert.getMessage(),
                        alert.getAlertMonth(),
                        alert.getDismissedAt() != null,
                        alert.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void dismissAlert(Long alertId) {

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Alert not found"));

        alert.setDismissedAt(java.time.LocalDateTime.now());
        alertRepository.save(alert);
    }
    
    @Transactional
    public void generateRentOverdueAlerts() {

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate gracePeriodEnd = monthStart.plusDays(3);

        if (today.isBefore(gracePeriodEnd)) {
            return;
        }

        var overdueUnits =
                rentPaymentRepository.findUnitsWithoutFullPayment(monthStart);

        for (Unit unit : overdueUnits) {
            createRentOverdueAlert(unit, monthStart);
        }
    }
}
