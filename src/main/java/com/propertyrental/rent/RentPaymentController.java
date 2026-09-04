package com.propertyrental.rent;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.propertyrental.ResourceNotFoundException;

@RestController
@RequestMapping("/api/rent")
public class RentPaymentController {

    private final RentPaymentService rentPaymentService;

    public RentPaymentController(RentPaymentService rentPaymentService) {
        this.rentPaymentService = rentPaymentService;
    }

    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping("/units/{unitId}/payments")
    public RentPayment recordPayment(
            @PathVariable Long unitId,
            @Valid @RequestBody RecordRentPaymentRequest request,
            Authentication authentication) {

        return rentPaymentService.recordPayment(
                unitId,
                request,
                authentication.getName()
        );
    }
    
    @GetMapping("/units/{unitId}/payments")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public List<RentPayment> getPaymentsForUnit(@PathVariable Long unitId) {
        return rentPaymentService.getPaymentsForUnit(unitId);
    }
    
    @GetMapping("/units/{unitId}/payments/{paymentMonth}")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public RentPayment getPaymentForMonth(
            @PathVariable Long unitId,
            @PathVariable String paymentMonth) {

        LocalDate month = LocalDate.parse(paymentMonth);

        return rentPaymentService
                .getPaymentForMonth(unitId, month)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No rent payment found for unit "
                                        + unitId
                                        + " and month "
                                        + paymentMonth));
    }
    
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public BulkRentPaymentResponse recordBulkPayments(
            @Valid @RequestBody BulkRentPaymentRequest request,
            Authentication authentication) {

        return rentPaymentService.recordBulkPayments(
                request,
                authentication.getName());
    }
    
    @GetMapping(value = "/rent-roll", produces = "text/csv")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<String> generateRentRoll(
            @RequestParam LocalDate paymentMonth) {

        String csv = rentPaymentService.generateRentRoll(paymentMonth);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=rent-roll-" + paymentMonth + ".csv")
                .body(csv);
    }
}
