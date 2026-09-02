package com.propertyrental.unit;

import org.springframework.stereotype.Service;
import com.propertyrental.ResourceNotFoundException;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
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
}