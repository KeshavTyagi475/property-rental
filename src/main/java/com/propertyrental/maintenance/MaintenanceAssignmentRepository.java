package com.propertyrental.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceAssignmentRepository
        extends JpaRepository<MaintenanceAssignment, Long> {
}
