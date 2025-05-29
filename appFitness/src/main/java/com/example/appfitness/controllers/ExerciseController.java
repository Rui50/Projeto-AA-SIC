package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.Exercise.ExerciseResponseDTO;
import com.example.appfitness.models.Exercise;
import com.example.appfitness.services.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // se quisermos criar exercicios fazer um post

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDTO>> getAllExercises() {
        List<Exercise> exercises = exerciseService.findAll();

        // se usarmos api e tiver "down"
        if(exercises.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<ExerciseResponseDTO> dtos = exercises.stream()
                .map(ExerciseResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getExerciseById(@PathVariable Integer id) {
        Optional<Exercise> exercise = exerciseService.findById(id);

        if (exercise.isPresent()) {
            ExerciseResponseDTO responseDTO = ExerciseResponseDTO.fromEntity(exercise.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
