package com.propertyrental.alert;

import com.propertyrental.unit.Unit;
import com.propertyrental.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "alerts",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_alerts_unit_type_month",
            columnNames = {"unit_id", "alert_type", "alert_month"}
        )
    }
)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 30)
    private AlertType alertType;

    @Column(name = "alert_month", nullable = false)
    private LocalDate alertMonth;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissed_by")
    private User dismissedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Alert() {
    }

    public Long getId() {
        return id;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public LocalDate getAlertMonth() {
        return alertMonth;
    }

    public void setAlertMonth(LocalDate alertMonth) {
        this.alertMonth = alertMonth;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDismissedAt() {
        return dismissedAt;
    }

    public void setDismissedAt(LocalDateTime dismissedAt) {
        this.dismissedAt = dismissedAt;
    }

    public User getDismissedBy() {
        return dismissedBy;
    }

    public void setDismissedBy(User dismissedBy) {
        this.dismissedBy = dismissedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}