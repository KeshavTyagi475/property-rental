package com.propertyrental.rent;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public List<RentPayment> getPaymentsForUnit(@PathVariable Long unitId) {
        return rentPaymentService.getPaymentsForUnit(unitId);
    }
}
