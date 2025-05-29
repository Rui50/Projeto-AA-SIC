package com.example.appfitness.controllers;

import com.example.appfitness.services.BodyMetricsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bodyMetrics")
public class BodyMetricsController {

    private BodyMetricsService bodyMetricsService;


}
