package com.propertyrental.rent;

import com.propertyrental.ResourceNotFoundException;
import com.propertyrental.unit.Unit;
import com.propertyrental.unit.UnitRepository;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentPaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    public RentPaymentService(
            RentPaymentRepository rentPaymentRepository,
            UnitRepository unitRepository,
            UserRepository userRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
    }

    public RentPayment recordPayment(
            Long unitId,
            RecordRentPaymentRequest request,
            String username) {

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unit not found: " + unitId));

        User recordedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + username));

        RentPayment payment = new RentPayment();
        payment.setUnit(unit);
        payment.setPaymentMonth(request.paymentMonth());
        payment.setAmount(request.amount());
        payment.setRecordedBy(recordedBy);
        payment.setRecordedAt(LocalDate.now().atStartOfDay());

        return rentPaymentRepository.save(payment);
    }
    
    public List<RentPayment> getPaymentsForUnit(Long unitId) {
        if (!unitRepository.existsById(unitId)) {
            throw new ResourceNotFoundException("Unit not found: " + unitId);
        }
        return rentPaymentRepository.findByUnitIdOrderByPaymentMonthDesc(unitId);
    }
}
