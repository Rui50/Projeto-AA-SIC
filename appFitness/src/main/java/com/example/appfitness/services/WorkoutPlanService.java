package com.example.appfitness.services;

import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutPlan;
import com.example.appfitness.repositories.ExerciseRepository;
import com.example.appfitness.repositories.ProfessorRepository;
import com.example.appfitness.repositories.UserRepository;
import com.example.appfitness.repositories.WorkoutPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Serviço responsavel por gerenciar o Workoutplan, exerciseData e SetData
 */
@Service
public class WorkoutPlanService {
    private WorkoutPlanRepository workoutPlanRepository;
    private UserRepository userRepository;
    private ProfessorRepository professorRepository;
    private ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan, Integer ownerId, Integer creatorId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner user not found " + ownerId));

        workoutPlan.setOwner(owner);
        workoutPlan.setCreatedBy(creatorId);

        // doesnt start as active
        workoutPlan.setActive(false);
        workoutPlan.setExercises(new ArrayList<>());


        return workoutPlanRepository.save(workoutPlan);
    }

    @Transactional
    public WorkoutPlan updateWorkoutPlan(WorkoutPlan workoutPlan) {
        return workoutPlanRepository.save(workoutPlan);
    }


    @Transactional
    public void deleteWorkoutPlanById(Integer id) {
        workoutPlanRepository.deleteById(id);
    }


    // um metodo so de update ou metemos metodo para adicionar exericicio/remover etc
}
