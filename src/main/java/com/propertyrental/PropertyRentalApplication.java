package com.propertyrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PropertyRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyRentalApplication.class, args);
    }
}
