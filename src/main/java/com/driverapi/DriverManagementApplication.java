package com.driverapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DriverManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverManagementApplication.class, args);
    }
}