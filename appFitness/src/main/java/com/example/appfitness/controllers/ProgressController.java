package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.Progress.ProgressDashboardDTO;
import com.example.appfitness.DTOs.Progress.ProgressRequestDTO;
import com.example.appfitness.DTOs.Progress.ProgressStatsDTO;
import com.example.appfitness.DTOs.Progress.WorkoutExecutionDTO;
import com.example.appfitness.services.ProgressService;
import com.example.appfitness.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private ProgressService progressService;
    private UserService userService;

    public ProgressController(ProgressService progressService, UserService userService) {
        this.progressService = progressService;
        this.userService = userService;
    }

    @PostMapping("/dashboard")
    public ProgressDashboardDTO getProgressDashboard(@RequestBody ProgressRequestDTO progressRequestDTO) {
        System.out.println("Received ProgressRequestDTO for /dashboard: " + progressRequestDTO.toString());
        return progressService.getProgress(progressRequestDTO);
    }

    @GetMapping("/stats")
    public ProgressStatsDTO getProgressStats(@RequestBody ProgressRequestDTO progressRequestDTO) {
        return progressService.getProgressStats(progressRequestDTO);
    }

    @GetMapping("/recent-workouts")
    private List<WorkoutExecutionDTO> getRecentWorkouts(@RequestBody ProgressRequestDTO progressRequestDTO) {
        return progressService.getRecentWorkouts(progressRequestDTO);
    }
}
