package com.propertyrental.unit;

import org.springframework.stereotype.Service;
import com.propertyrental.ResourceNotFoundException;
import java.util.List;
import java.time.LocalDateTime;
import com.propertyrental.maintenance.MaintenanceRequest;
import com.propertyrental.maintenance.MaintenanceRequestRepository;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public UnitService(
            UnitRepository unitRepository,
            MaintenanceRequestRepository maintenanceRequestRepository) {

        this.unitRepository = unitRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
    }

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    public Unit getUnit(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unit not found: " + id));
    }
    
    public Unit createUnit(CreateUnitRequest request) {
        Unit unit = new Unit();

        unit.setUnitNumber(request.unitNumber());
        unit.setAddress(request.address());
        unit.setMonthlyRent(request.monthlyRent());
        unit.setCurrentTenant(request.currentTenant());
        unit.setArchived(false);
        
        LocalDateTime now = LocalDateTime.now();
        unit.setCreatedAt(now);
        unit.setUpdatedAt(now);
        return unitRepository.save(unit);
    }
    
    public Unit updateUnit(Long id, UpdateUnitRequest request) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unit not found: " + id));

        unit.setUnitNumber(request.unitNumber());
        unit.setAddress(request.address());
        unit.setMonthlyRent(request.monthlyRent());
        unit.setCurrentTenant(request.currentTenant());
        unit.setUpdatedAt(java.time.LocalDateTime.now());
        return unitRepository.save(unit);
    }
    
    public Unit archiveUnit(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unit not found: " + id));

        unit.setArchived(true);
        unit.setUpdatedAt(LocalDateTime.now());
        return unitRepository.save(unit);
    }
    
    public Unit restoreUnit(Long id) {
        Unit unit = unitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + id));

        unit.setArchived(false);
        unit.setUpdatedAt(LocalDateTime.now());
        return unitRepository.save(unit);
    }
    
    public List<MaintenanceRequest> getMaintenanceRequests(Long unitId) {

        if (!unitRepository.existsById(unitId)) {
            throw new ResourceNotFoundException(
                    "Unit not found: " + unitId);
        }

        return maintenanceRequestRepository
                .findByUnitIdOrderByCreatedAtDesc(unitId);
    }
}