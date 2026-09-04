package com.propertyrental.unit;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public List<Unit> getAllUnits() {
        return unitService.getAllUnits();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public Unit getUnit(@PathVariable Long id) {
        return unitService.getUnit(id);
    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Unit createUnit(
            @Valid @RequestBody CreateUnitRequest request) {

        return unitService.createUnit(request);
    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PutMapping("/{id}")
    public Unit updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitRequest request) {

        return unitService.updateUnit(id, request);
    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping("/{id}/archive")
    public Unit archiveUnit(@PathVariable Long id) {
        return unitService.archiveUnit(id);
    }
    
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping("/{id}/restore")
    public Unit restoreUnit(@PathVariable Long id) {
        return unitService.restoreUnit(id);
    }
}