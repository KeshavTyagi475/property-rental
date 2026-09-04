package com.propertyrental.alert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByDismissedAtIsNullOrderByCreatedAtDesc();

    List<Alert> findByDismissedAtIsNullAndAlertTypeOrderByCreatedAtDesc(
            AlertType alertType);

    Optional<Alert> findByUnitIdAndAlertTypeAndAlertMonth(
            Long unitId,
            AlertType alertType,
            LocalDate alertMonth);
}
