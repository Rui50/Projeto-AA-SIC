package com.example.appfitness.services;

import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanRequestDTO;
import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsavel por gerenciar o Workoutplan, exerciseData e SetData
 */
@Service
public class WorkoutPlanService {
    private WorkoutPlanRepository workoutPlanRepository;
    private UserRepository userRepository;
    private ProfessorRepository professorRepository;
    private ExerciseRepository exerciseRepository;
    private ExerciseDataRepository exerciseDataRepository;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ProfessorRepository professorRepository,
                              ExerciseRepository exerciseRepository,
                              UserRepository userRepository,
                              ExerciseDataRepository exerciseDataRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.professorRepository = professorRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.exerciseDataRepository = exerciseDataRepository;
    }

    @Transactional
    public List<WorkoutPlan> getWorkoutPlansByOwnerId(Integer ownerId) {
        return workoutPlanRepository.findByOwnerId(ownerId);
    }

    @Transactional
    public WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan, Integer ownerId, Integer creatorId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner user not found " + ownerId));
        workoutPlan.setOwner(owner);
        workoutPlan.setCreatedBy(creatorId);

        if (workoutPlan.getScheduleType() == null) {
            workoutPlan.setScheduleType(WorkoutPlan.WorkoutScheduleType.FREE); // Or FIXED, based on your business logic
        }

        // doesnt start as active
        workoutPlan.setActive(false);
        workoutPlan.setExercises(new ArrayList<>());
        workoutPlan.setUpdatedAt(LocalDate.now());

        return workoutPlanRepository.save(workoutPlan);
    }

    public Optional<WorkoutPlan> getWorkoutPlanById(Integer id) {
        return workoutPlanRepository.findById(id);
    }

    @Transactional
    public WorkoutPlan updateWorkoutPlan(Integer workoutPlanId, WorkoutPlanRequestDTO updateDTO) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        workoutPlan.setName(updateDTO.getName());
        workoutPlan.setDescription(updateDTO.getDescription());
        workoutPlan.setScheduleType(updateDTO.getScheduleType());
        workoutPlan.setScheduledDays(updateDTO.getScheduledDays());
        workoutPlan.setUpdatedAt(LocalDate.now());

        // secalhar algumas coisas fazemos o update de outra maneira tipo o active
        workoutPlan.setActive(updateDTO.isActive());

        return workoutPlanRepository.save(workoutPlan);
    }


    @Transactional
    public void deleteWorkoutPlanById(Integer id) {
        if (!workoutPlanRepository.existsById(id)) {
            throw new RuntimeException("Workout Plan not found: " + id);
        }
        workoutPlanRepository.deleteById(id);
    }



    // um metodo so de update ou metemos metodo para adicionar exericicio/remover etc

    @Transactional
    public ExerciseData addExerciseToWorkoutPlan(Integer workoutPlanId, Integer exerciseId, List<SetData> plannedSets, String note) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found: " + exerciseId));

        ExerciseData exerciseData = new ExerciseData();
        exerciseData.setExercise(exercise);
        exerciseData.setWorkoutPlan(workoutPlan);
        exerciseData.setNote(note);

        for(SetData setData : plannedSets) {
            exerciseData.addPlannedSet(setData);
        }

        workoutPlan.getExercises().add(exerciseData);
        workoutPlanRepository.save(workoutPlan); // cascade saves the rest?

        return exerciseDataRepository.save(exerciseData);
    }

    @Transactional
    public ExerciseData updateExerciseInWorkoutPlan(Integer workoutPlanId, Integer exerciseDataId, List<SetData> updatedPlannedSets, String updatedNote) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found " + workoutPlanId));

        ExerciseData exerciseData = exerciseDataRepository.findById(exerciseDataId)
                .orElseThrow(() -> new RuntimeException("Exercise Data not: " + exerciseDataId));

        // testar erro na frontend
        if (exerciseData.getWorkoutPlan().getId() != workoutPlanId) {
            throw new RuntimeException("Exercise Data " + exerciseDataId + " not from Workout Plan " + workoutPlanId);
        }

        exerciseData.setNote(updatedNote);
        // for now just fully replace
        exerciseData.getPlannedSets().clear();
        for (SetData setData : updatedPlannedSets) {
            exerciseData.addPlannedSet(setData);
        }
        return exerciseDataRepository.save(exerciseData);
    }

    @Transactional
    public void removeExerciseFromWorkoutPlan(Integer workoutPlanId, Integer exerciseDataId) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        ExerciseData exerciseDataToRemove = exerciseDataRepository.findById(exerciseDataId)
                .orElseThrow(() -> new RuntimeException("Exercise Data not found: " + exerciseDataId));

        if (exerciseDataToRemove.getWorkoutPlan().getId() != workoutPlanId) {
            throw new RuntimeException("Exercise Data " + exerciseDataId + " not from Workout Plan " + workoutPlanId);
        }

        workoutPlan.getExercises().remove(exerciseDataToRemove);
        workoutPlanRepository.save(workoutPlan);

        exerciseDataRepository.delete(exerciseDataToRemove);
    }

}


