package com.propertyrental.rent;

import com.propertyrental.ResourceNotFoundException;
import com.propertyrental.unit.Unit;
import com.propertyrental.unit.UnitRepository;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    public Optional<RentPayment> getPaymentForMonth(
            Long unitId,
            LocalDate paymentMonth) {

        if (!unitRepository.existsById(unitId)) {
            throw new ResourceNotFoundException("Unit not found: " + unitId);
        }

        return rentPaymentRepository.findByUnitIdAndPaymentMonth(
                unitId,
                paymentMonth
        );
    }
    
    @Transactional
    public BulkRentPaymentResponse recordBulkPayments(
            BulkRentPaymentRequest request,
            String username) {

        User recordedBy = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<BulkRentPaymentResult> results = new ArrayList<>();

        for (BulkRentPaymentItem item : request.payments()) {

            Optional<Unit> unitOptional =
                    unitRepository.findByUnitNumber(item.unitNumber());

            if (unitOptional.isEmpty()) {
                results.add(new BulkRentPaymentResult(
                        item.unitNumber(),
                        null,
                        item.amount(),
                        null,
                        "UNMATCHED"
                ));
                continue;
            }

            Unit unit = unitOptional.get();

            String status;

            int comparison =
                    item.amount().compareTo(unit.getMonthlyRent());

            if (comparison == 0) {
                status = "MATCHED";
            } else if (comparison < 0) {
                status = "UNDERPAID";
            } else {
                status = "OVERPAID";
            }

            RentPayment payment = new RentPayment();
            payment.setUnit(unit);
            payment.setPaymentMonth(request.paymentMonth());
            payment.setAmount(item.amount());
            payment.setRecordedBy(recordedBy);
            payment.setRecordedAt(LocalDateTime.now());

            rentPaymentRepository.save(payment);

            results.add(new BulkRentPaymentResult(
                    unit.getUnitNumber(),
                    unit.getMonthlyRent(),
                    item.amount(),
                    unit.getCurrentTenant(),
                    status
            ));
        }

        return new BulkRentPaymentResponse(
                request.paymentMonth(),
                results
        );
    }
    
    @Transactional(readOnly = true)
    public String generateRentRoll(LocalDate paymentMonth) {

        List<Unit> units = unitRepository.findAll(
                Sort.by(Sort.Direction.ASC, "unitNumber"));

        StringBuilder csv = new StringBuilder();

        csv.append("Unit Number,Address,Monthly Rent,Tenant,")
           .append("Payment Amount,Payment Status\n");

        for (Unit unit : units) {

            Optional<RentPayment> payment =
                    rentPaymentRepository
                            .findByUnitIdAndPaymentMonth(
                                    unit.getId(),
                                    paymentMonth);

            BigDecimal paymentAmount =
                    payment.map(RentPayment::getAmount)
                           .orElse(null);

            String paymentStatus;

            if (paymentAmount == null) {
                paymentStatus = "UNPAID";
            } else {
                int comparison =
                        paymentAmount.compareTo(unit.getMonthlyRent());

                if (comparison == 0) {
                    paymentStatus = "MATCHED";
                } else if (comparison < 0) {
                    paymentStatus = "UNDERPAID";
                } else {
                    paymentStatus = "OVERPAID";
                }
            }

            csv.append(csvValue(unit.getUnitNumber())).append(",");
            csv.append(csvValue(unit.getAddress())).append(",");
            csv.append(unit.getMonthlyRent()).append(",");
            csv.append(csvValue(unit.getCurrentTenant())).append(",");
            csv.append(paymentAmount == null ? "" : paymentAmount).append(",");
            csv.append(paymentStatus).append("\n");
        }

        return csv.toString();
    }
    
    private String csvValue(String value) {

        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        return "\"" + escaped + "\"";
    }
}
