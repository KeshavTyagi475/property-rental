package com.propertyrental;

import com.propertyrental.user.Role;
import com.propertyrental.user.User;
import com.propertyrental.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername("manager").isEmpty()) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setPasswordHash(
                        passwordEncoder.encode("manager123")
                );
                manager.setRole(Role.PROPERTY_MANAGER);
                manager.setCreatedAt(LocalDateTime.now());

                userRepository.save(manager);
            }

            if (userRepository.findByUsername("contractor").isEmpty()) {
                User contractor = new User();
                contractor.setUsername("contractor");
                contractor.setPasswordHash(
                        passwordEncoder.encode("contractor123")
                );
                contractor.setRole(Role.MAINTENANCE_CONTRACTOR);
                contractor.setCreatedAt(LocalDateTime.now());

                userRepository.save(contractor);
            }
        };
    }
}