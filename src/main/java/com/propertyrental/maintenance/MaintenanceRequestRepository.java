package com.propertyrental.maintenance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long>, JpaSpecificationExecutor<MaintenanceRequest>  {
	List<MaintenanceRequest> findDistinctByAssignmentsContractorId(Long contractorId);
}