package com.propertyrental.maintenance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceAssignmentRepository
        extends JpaRepository<MaintenanceAssignment, Long> {
	Optional<MaintenanceAssignment>
	findByMaintenanceRequestIdAndContractorId(
	        Long requestId,
	        Long contractorId
	);
	
	void deleteByMaintenanceRequestIdAndContractorId(
	        Long requestId,
	        Long contractorId
	);
}
