package com.example.appfitness.controllers;

import com.example.appfitness.services.ExerciseService;
import com.example.appfitness.services.UserService;
import com.example.appfitness.services.WorkoutExecutionService;
import com.example.appfitness.services.WorkoutPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workout-executions")
public class WorkoutExecutionController {
    private WorkoutExecutionService workoutExecutionService;
    private UserService userService;
    private WorkoutPlanService workoutPlanService;
    private ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<Object> startWorkout(@RequestBody WorkoutExecutionStartDTO startDTO) {

    }

    @PutMapping("/{executionId}/finish")
    public ResponseEntity<WorkoutExecutionResponseDTO> finishWorkout(
            @PathVariable Integer executionId,
            @RequestBody WorkoutExecutionFinishDTO finishDTO) {

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getWorkoutExecutionById(@PathVariable Integer id) {

    }

    // provavelmente nao é necessário, mas caso seja
    //@PostMapping("/{executionId}/exercises/{exerciseDataId}/start")
    //@PutMapping("/{executionId}/exercises/{exerciseExecutionId}/finish")

    @PostMapping("/{executionId}/exercises/{exerciseExecutionId}/sets")
    public ResponseEntity<SetExecutionResponseDTO> recordSetExecution(
            @PathVariable Integer executionId,
            @PathVariable Integer exerciseExecutionId,
            @RequestBody SetExecutionRequestDTO setExecutionDTO) {

    }




