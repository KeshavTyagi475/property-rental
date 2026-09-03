package com.propertyrental.rent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
	List<RentPayment> findByUnitIdOrderByPaymentMonthDesc(Long unitId);
	
	Optional<RentPayment> findByUnitIdAndPaymentMonth(
	        Long unitId,
	        LocalDate paymentMonth
	);
}
