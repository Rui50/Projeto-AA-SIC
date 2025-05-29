package com.example.appfitness.services;

import com.example.appfitness.models.Exercise;
import com.example.appfitness.repositories.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    private ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public Optional<Exercise> findById(Integer id) {
        return exerciseRepository.findById(id);
    }

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }
}
