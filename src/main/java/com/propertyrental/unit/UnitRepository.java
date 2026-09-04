package com.propertyrental.unit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, Long> {
	Optional<Unit> findByUnitNumber(String unitNumber);
}