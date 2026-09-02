package com.propertyrental.rent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
	List<RentPayment> findByUnitIdOrderByPaymentMonthDesc(Long unitId);
}
