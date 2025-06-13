package com.example.appfitness;

import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppFitnessApplication {

    @PostConstruct
    void started() {
        // Set the default time zone to Portugal
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Lisbon"));
    }

    public static void main(String[] args) {
        SpringApplication.run(AppFitnessApplication.class, args);
    }
}