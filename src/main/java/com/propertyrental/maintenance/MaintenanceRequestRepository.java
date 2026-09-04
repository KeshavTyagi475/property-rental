package com.propertyrental.maintenance;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaintenanceRequestRepository
        extends JpaRepository<MaintenanceRequest, Long>, JpaSpecificationExecutor<MaintenanceRequest>  {
	List<MaintenanceRequest> findDistinctByAssignmentsContractorId(Long contractorId);
	List<MaintenanceRequest> findByUnitIdOrderByCreatedAtDesc(Long unitId);
	
	@Query("""
	        SELECT COUNT(m)
	        FROM MaintenanceRequest m
	        WHERE m.status <> com.propertyrental.maintenance.Status.RESOLVED
	        """)
	long countOpenMaintenanceRequests();
	
	@Query("""
	        SELECT COUNT(m)
	        FROM MaintenanceRequest m
	        WHERE m.status = com.propertyrental.maintenance.Status.RESOLVED
	          AND m.updatedAt >= :start
	          AND m.updatedAt < :end
	        """)
	long countResolvedBetween(
	        @Param("start") LocalDateTime start,
	        @Param("end") LocalDateTime end);
	
	@Query("""
	        SELECT m.status, COUNT(m)
	        FROM MaintenanceRequest m
	        GROUP BY m.status
	        """)
	List<Object[]> countMaintenanceByStatus();
	
	@Query("""
	        SELECT u.username, COUNT(DISTINCT m.id)
	        FROM MaintenanceRequest m
	        JOIN m.assignments a
	        JOIN a.contractor u
	        WHERE u.role = com.propertyrental.user.Role.MAINTENANCE_CONTRACTOR
	        GROUP BY u.username
	        ORDER BY u.username
	        """)
	List<Object[]> countMaintenanceByContractor();
}