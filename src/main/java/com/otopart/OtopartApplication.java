package com.otopart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OtopartApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtopartApplication.class, args);
    }
}
