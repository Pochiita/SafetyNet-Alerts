package com.safetynet.alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.safetynet.alerts")
public class AlertsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertsApplication.class, args);
    }

}
