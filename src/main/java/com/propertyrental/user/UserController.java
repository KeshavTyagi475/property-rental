package com.propertyrental.user;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/contractors")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public List<User> getContractors() {
        return userRepository.findByRole(Role.MAINTENANCE_CONTRACTOR);
    }
}