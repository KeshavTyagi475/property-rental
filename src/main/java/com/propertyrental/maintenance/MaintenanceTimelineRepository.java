package com.propertyrental.maintenance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceTimelineRepository
        extends JpaRepository<MaintenanceTimeline, Long> {
	List<MaintenanceTimeline> findByMaintenanceRequestIdOrderByCreatedAtAscIdAsc(Long maintenanceRequestId);
}