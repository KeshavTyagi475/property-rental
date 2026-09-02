package com.propertyrental.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceTimelineRepository
        extends JpaRepository<MaintenanceTimeline, Long> {
}