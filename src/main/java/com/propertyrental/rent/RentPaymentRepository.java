package com.propertyrental.rent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.propertyrental.unit.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {
	List<RentPayment> findByUnitIdOrderByPaymentMonthDesc(Long unitId);
	
	Optional<RentPayment> findByUnitIdAndPaymentMonth(
	        Long unitId,
	        LocalDate paymentMonth
	);
	
	@Query("""
	        SELECT COALESCE(SUM(r.amount), 0)
	        FROM RentPayment r
	        WHERE r.paymentMonth = :month
	        """)
	BigDecimal sumPaymentsForMonth(
	        @Param("month") LocalDate month);
	
	@Query("""
	        SELECT COUNT(u)
	        FROM Unit u
	        WHERE u.archived = false
	          AND NOT EXISTS (
	              SELECT r.id
	              FROM RentPayment r
	              WHERE r.unit.id = u.id
	                AND r.paymentMonth = :month
	                AND r.amount >= u.monthlyRent
	          )
	        """)
	long countUnitsWithoutFullPayment(
	        @Param("month") LocalDate month);
	
	@Query("""
		    SELECT u
		    FROM Unit u
		    WHERE u.archived = false
		    AND NOT EXISTS (
		        SELECT r.id
		        FROM RentPayment r
		        WHERE r.unit.id = u.id
		        AND r.paymentMonth = :month
		        AND r.amount >= u.monthlyRent
		    )
		    """)
		List<Unit> findUnitsWithoutFullPayment(@Param("month") LocalDate month);
}
