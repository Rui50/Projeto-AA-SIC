package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.WorkoutPlan.ExerciseDataRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.ExerciseDataResponseDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanResponseDTO;
import com.example.appfitness.models.ExerciseData;
import com.example.appfitness.models.SetData;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.models.WorkoutPlan;
import com.example.appfitness.services.ExerciseService;
import com.example.appfitness.services.ProfessorService;
import com.example.appfitness.services.UserService;
import com.example.appfitness.services.WorkoutPlanService;
import org.hibernate.jdbc.Work;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {
    private WorkoutPlanService workoutPlanService;
    private UserService userService;
    private ExerciseService exerciseService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService, UserService userService, ProfessorService professorService, ExerciseService exerciseService) {
        this.workoutPlanService = workoutPlanService;
        this.userService = userService;
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<Object> createWorkoutPlan(@RequestBody WorkoutPlanRequestDTO createDTO) {
        Integer creatorId = createDTO.getOwnerId();

        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setName(createDTO.getName());
        workoutPlan.setDescription(createDTO.getDescription());
        workoutPlan.setScheduleType(createDTO.getScheduleType());
        workoutPlan.setScheduledDays(createDTO.getScheduledDays());

        // service handles the rest for now, if we need the create to already bring the exercises we should put all the logic here
        WorkoutPlan savedWorkoutPlan = workoutPlanService.createWorkoutPlan(workoutPlan, createDTO.getOwnerId(), creatorId);

        return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutPlanResponseDTO.fromEntity(savedWorkoutPlan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getWorkoutPlanById(@PathVariable Integer id) {
        Optional<WorkoutPlan> workoutPlan = workoutPlanService.getWorkoutPlanById(id);

        if (workoutPlan.isPresent()) {
            return ResponseEntity.ok(WorkoutPlanResponseDTO.fromEntity(workoutPlan.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutPlan(@PathVariable Integer id) {
        try {
            workoutPlanService.deleteWorkoutPlanById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutPlanResponseDTO> updateWorkoutPlan(
            @PathVariable Integer id,
            @RequestBody WorkoutPlanRequestDTO updateDTO) {
        WorkoutPlan updatedWorkoutPlan = workoutPlanService.updateWorkoutPlan(id, updateDTO);

        return ResponseEntity.ok(WorkoutPlanResponseDTO.fromEntity(updatedWorkoutPlan));
    }

    @PostMapping("/{workoutPlanId}/exercises")
    public ResponseEntity<ExerciseDataResponseDTO> addExerciseToWorkoutPlan(
            @PathVariable Integer workoutPlanId,
            @RequestBody ExerciseDataRequestDTO exerciseDataDTO){

        List<SetData> plannedSets = exerciseDataDTO.getPlannedSets().stream()
                .map(dto -> {
                    SetData setData = new SetData();
                    setData.setSetNumber(dto.getSetNumber());
                    setData.setRepsPlanned(dto.getReps());
                    setData.setWeightPlanned(dto.getWeight());
                    setData.setRestTimeSuggestion(dto.getRestTimeSugested());
                    return setData;
                }).toList();

        ExerciseData addedExerciseData = workoutPlanService.addExerciseToWorkoutPlan(
                workoutPlanId,
                exerciseDataDTO.getExerciseId(),
                plannedSets,
                exerciseDataDTO.getNote()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseDataResponseDTO.fromEntity(addedExerciseData));
    }

    @PutMapping("/{workoutPlanId}/exercises/{exerciseDataId}")
    public ResponseEntity<ExerciseDataResponseDTO> updateExerciseInWorkoutPlan(
            @PathVariable Integer workoutPlanId,
            @PathVariable Integer exerciseDataId,
            @RequestBody ExerciseDataRequestDTO updatedExerciseDataDTO) {

        List<SetData> plannedSets = updatedExerciseDataDTO.getPlannedSets().stream()
                .map(dto -> {
                    SetData setData = new SetData();
                    setData.setSetNumber(dto.getSetNumber());
                    setData.setRepsPlanned(dto.getReps());
                    setData.setWeightPlanned(dto.getWeight());
                    setData.setRestTimeSuggestion(dto.getRestTimeSugested());
                    return setData;
                }).collect(Collectors.toList());

        ExerciseData updatedExerciseData = workoutPlanService.updateExerciseInWorkoutPlan(
                workoutPlanId,
                exerciseDataId,
                plannedSets,
                updatedExerciseDataDTO.getNote()
        );

        return ResponseEntity.ok(ExerciseDataResponseDTO.fromEntity(updatedExerciseData));
    }

    @DeleteMapping("/{workoutPlanId}/exercises/{exerciseDataId}")
    public ResponseEntity<Void> removeExerciseFromWorkoutPlan(
            @PathVariable Integer workoutPlanId,
            @PathVariable Integer exerciseDataId) {
        try {
            workoutPlanService.removeExerciseFromWorkoutPlan(workoutPlanId, exerciseDataId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
