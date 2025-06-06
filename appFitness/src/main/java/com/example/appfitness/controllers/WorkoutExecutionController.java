package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.WorkoutExecution.*;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.services.ExerciseService;
import com.example.appfitness.services.UserService;
import com.example.appfitness.services.WorkoutExecutionService;
import com.example.appfitness.services.WorkoutPlanService;
import jakarta.persistence.EntityNotFoundException;
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

    @GetMapping("/in-progress/{userId}")
    public ResponseEntity<Boolean> hasWorkoutInProgress(@PathVariable Integer userId) {
        boolean inProgress = workoutExecutionService.hasWorkoutInProgress(userId);
        return ResponseEntity.ok(inProgress);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startWorkout(@RequestBody WorkoutExecutionStartRequestDTO startDTO) {
        try {
            System.out.println("Received request to start workout. DTO: " + startDTO.toString());

            if (startDTO.getUserId() == null || startDTO.getWorkoutPlanId() == null) {
                System.err.println("ERROR: Invalid startDTO: userId or workoutPlanId is null. DTO: " + startDTO.toString());
                return ResponseEntity.badRequest().build();
            }

            // Verificação de workout em andamento
            if (workoutExecutionService.hasWorkoutInProgress(startDTO.getUserId())) {
                System.err.println("User already has a workout in progress. Blocking new workout.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already has a workout in progress.");
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
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/sets/{setExecutionId}")
    public ResponseEntity<SetExecutionResponseDTO> updateSetExecution(
            @PathVariable Integer setExecutionId,
            @RequestBody SetExecutionRequestDTO setExecutionDTO) {
        try {
            SetExecution setExecutionDetails = new SetExecution();
            setExecutionDetails.setRepsPerformed(setExecutionDTO.getRepsPerformed());
            setExecutionDetails.setWeightPerformed(setExecutionDTO.getWeightPerformed());
            setExecutionDetails.setResTimePerformed(setExecutionDTO.getRestTimePerformed());

            SetExecution updatedSet = workoutExecutionService.updateSetExecution(
                    setExecutionId,
                    setExecutionDetails
            );
            return ResponseEntity.ok(SetExecutionResponseDTO.fromEntity(updatedSet));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            System.err.println("Error updating set execution " + setExecutionId + ": " + e.getMessage());
            return ResponseEntity.badRequest().build();
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
        try {
            System.out.println("Received request to retrieve workout execution by id. ID: " + id);
            WorkoutExecutionResponseDTO responseDTO = workoutExecutionService.getWorkoutExecutionById(id);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    @PostMapping("/all")
    public ResponseEntity<AllWorkoutsResponseDTO> getAllWorkouts(@RequestBody AllWorkoutsRequestDTO request) {
        try {
            System.out.println("Received request to retrieve all workouts. DTO: " + request.toString());
            AllWorkoutsResponseDTO response = workoutExecutionService.getAllWorkoutsForUser(request);
            return ResponseEntity.ok(response);
        }
        catch (RuntimeException e) {
            System.err.println("Error fetching all workouts: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}




