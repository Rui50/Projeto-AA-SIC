package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.WorkoutExecution.*;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.services.ExerciseService;
import com.example.appfitness.services.UserService;
import com.example.appfitness.services.WorkoutExecutionService;
import com.example.appfitness.services.WorkoutPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/workout-executions")
public class WorkoutExecutionController {
    // processo de execução
    
    // user inicia workout e faz call a /start
    // envia no body
    /**{
     "userId": 123,
     "workoutPlanId": 456
     }
     */
    // este controller recebe o startDTO eobtem o User e WORKOUTPLAN
    // verifica se existem algum em progresso se nao tiver cria um objeto WorkoutExecution (cascade trata do resto?)
    // Devolve resposta a frontend com o WorkoutExecutionResponseDTO
            
    // Parte de execução
    // depois da resposta de start, da fetch ao woorkoutExecution e o ref desse é populado
            // questão se fazemos post a cada set completado ou a cada exerci
            
            



    private WorkoutExecutionService workoutExecutionService;
    private UserService userService;
    private WorkoutPlanService workoutPlanService;
    private ExerciseService exerciseService;

    public WorkoutExecutionController(WorkoutExecutionService workoutExecutionService,
                                      UserService userService,
                                      WorkoutPlanService workoutPlanService,
                                      ExerciseService exerciseService) {
        this.workoutExecutionService = workoutExecutionService;
        this.userService = userService;
        this.workoutPlanService = workoutPlanService;
        this.exerciseService = exerciseService;
    }

    @PostMapping("/start")
    public ResponseEntity<WorkoutExecutionResponseDTO> startWorkout(@RequestBody WorkoutExecutionStartRequestDTO startDTO) {
        try {
            System.out.println("Received request to start workout. DTO: " + startDTO.toString());

            if (startDTO.getUserId() == null || startDTO.getWorkoutPlanId() == null) {
                System.err.println("ERROR: Invalid startDTO: userId or workoutPlanId is null. DTO: " + startDTO.toString());
                return null;
            }

            System.out.println("Calling workoutExecutionService.startWorkout with userId=" + startDTO.getUserId() + " and workoutPlanId=" + startDTO.getWorkoutPlanId());
            WorkoutExecution newWorkoutExec = workoutExecutionService.startWorkout(
                    startDTO.getUserId(),
                    startDTO.getWorkoutPlanId());

            System.out.println("Workout started successfully! New execution ID: " + newWorkoutExec.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutExecutionResponseDTO.fromEntity(newWorkoutExec));

        } catch (RuntimeException e) {
            System.err.println("ERROR: RuntimeException occurred while starting workout for DTO: " + startDTO.toString());
            e.printStackTrace();
            return null;
        }
    }

    /*
    @PostMapping("/start")
    public ResponseEntity<WorkoutExecutionResponseDTO> startWorkout(@RequestBody WorkoutExecutionStartRequestDTO startDTO) {
        try {

            WorkoutExecution newWorkoutExec = workoutExecutionService.startWorkout(
                    startDTO.getUserId(),
                    startDTO.getWorkoutPlanId());
            return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutExecutionResponseDTO.fromEntity(newWorkoutExec));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }*/
    @DeleteMapping("/{executionId}")
    public ResponseEntity<Void> deleteWorkoutExecution(@PathVariable Integer executionId) {
        try {
            workoutExecutionService.deleteWorkoutExecution(executionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            System.err.println("Error deleting workout execution " + executionId + ": " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{executionId}/finish")
    public ResponseEntity<WorkoutExecutionResponseDTO> finishWorkout(
            @PathVariable Integer executionId,
            @RequestBody WorkoutExecutionFinishRequestDTO finishDTO) {
        try {
            WorkoutExecution finishedExecution = workoutExecutionService.finishWorkout(
                    executionId,
                    finishDTO.getFeedback(),
                    finishDTO.getStatus()
            );
            return ResponseEntity.ok(WorkoutExecutionResponseDTO.fromEntity(finishedExecution));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutExecutionResponseDTO> getWorkoutExecutionById(@PathVariable Integer id) {
        Optional<WorkoutExecution> workoutExecution = workoutExecutionService.getWorkoutExecutionById(id);
        return workoutExecution.map(WorkoutExecutionResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // provavelmente nao é necessário, mas caso seja
    //@PostMapping("/{executionId}/exercises/{exerciseDataId}/start")
    //@PutMapping("/{executionId}/exercises/{exerciseExecutionId}/finish")

    @PostMapping("/exercises/{exerciseExecutionId}/sets")
    public ResponseEntity<SetExecutionResponseDTO> recordSetExecution(
            @PathVariable Integer exerciseExecutionId,
            @RequestBody SetExecutionRequestDTO setExecutionDTO) {
        try {
            SetExecution setExecution = new SetExecution();
            setExecution.setSetNumber(setExecutionDTO.getSetNumber());
            setExecution.setRepsPerformed(setExecutionDTO.getRepsPerformed());
            setExecution.setWeightPerformed(setExecutionDTO.getWeightPerformed());
            setExecution.setResTimePerformed(setExecutionDTO.getRestTimePerformed());

            SetExecution recordedSet = workoutExecutionService.recordSetExecution(
                    exerciseExecutionId,
                    setExecution,
                    setExecutionDTO.getPlannedSetId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(SetExecutionResponseDTO.fromEntity(recordedSet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}




