package com.example.appfitness.services;

import com.example.appfitness.models.*;
import com.example.appfitness.repositories.ExerciseDataRepository;
import com.example.appfitness.repositories.UserRepository;
import com.example.appfitness.repositories.WorkoutExecutionRepository;
import com.example.appfitness.repositories.WorkoutPlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responável por gerencial WOrkoutexecution, ExerciseExecution e SetExecution
 */
@Service
public class WorkoutExecutionService {
    private WorkoutExecutionRepository workoutExecutionRepository;
    private UserRepository userRepository;
    private WorkoutPlanRepository workoutPlanRepository;
    private ExerciseDataRepository exerciseDataRepository;

    @Transactional
    public WorkoutExecution startWorkout(Integer userId, Integer workoutPlanId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found" + userId));
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found " + workoutPlanId));

        // primeiro verificar se o utilizador já esta a meio de um treino
        Optional<WorkoutExecution> onGoingWorkout = workoutExecutionRepository.findByUserAndStatus(user, WorkoutExecution.WorkoutStatus.IN_PROGRESS);

        if(onGoingWorkout.isPresent()) {
            throw new RuntimeException("Theres an ongoing workout");
        }

        WorkoutExecution workout = new WorkoutExecution();

        workout.setUser(user);
        workout.setWorkoutPlan(workoutPlan);
        workout.setStartTime(LocalDateTime.now());
        workout.setStatus(WorkoutExecution.WorkoutStatus.IN_PROGRESS);



        return workoutExecutionRepository.save(workout);
    }

    @Transactional
    public WorkoutExecution finishWorkout(Integer userId, Integer workoutPlanId, Integer executionId) {
        WorkoutExecution execution = workoutExecutionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("WorkoutExecution not found with id " + executionId));

        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(WorkoutExecution.WorkoutStatus.COMPLETED);

        return workoutExecutionRepository.save(execution);
    }

    @Transactional
    public void deleteWorkoutExecutionById(Integer id) {
        workoutExecutionRepository.deleteById(id);
    }

    public List<WorkoutExecution> findWorkoutExecutionsByUser(Integer userid) {
        return workoutExecutionRepository.findByUserId(userid);
    }

    public List<WorkoutExecution> findWorkoutExecutionsByUserAndStatus(Integer userid, WorkoutExecution.WorkoutStatus status) {
        return workoutExecutionRepository.findByUserIdAndStatus(userid, status);
    }

    public Optional<WorkoutExecution> findLatestWorkoutExecutionForUser(Integer userid) {
        return workoutExecutionRepository.findTopByUserIdOrderByStartTimeDesc(userid).stream().findFirst();
    }

    public List<WorkoutExecution> findWorkoutExecutionsByUserAndPeriod(Integer userid, LocalDateTime start, LocalDateTime end) {
        return workoutExecutionRepository.findByUserIdAndStartTimeBetween(userid, start, end);
    }

    // vai ser preciso start exercise e end?

    //registar sets
    public SetExecution registerSetExecution(Integer workoutExecutionId, Integer exerciseExecutionId, SetExecution setExecutionDetails) {
        // find the workout
        WorkoutExecution workoutExecution = workoutExecutionRepository.findById(workoutExecutionId)
                .orElseThrow(() -> new RuntimeException("WorkoutExecution not found with id " + workoutExecutionId));

        // find the exercise
        ExerciseExecution ee = workoutExecution.getExerciseExecutions().stream()
                .filter(ex -> ex.getId() == exerciseExecutionId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ExerciseExecution not found  " + exerciseExecutionId));

        // buscar o set, se nao existe cria novo (user decidiu fazer um set a mais)
        Optional<SetExecution> set = ee.getPerformedSets().stream()
                .filter(s -> s.getSetNumber().equals(setExecutionDetails.getSetNumber()))
                .findFirst();

        SetExecution setExecution;

        if(set.isPresent()) {
            setExecution = set.get();
        }
        else {
            setExecution = new SetExecution();
            setExecution.setExerciseExecution(ee);
            setExecution.setSetNumber(setExecutionDetails.getSetNumber());
            ee.getPerformedSets().add(setExecution);
        }

        setExecution.setRepsPerformed(setExecutionDetails.getRepsPerformed());
        setExecution.setWeightPerformed(setExecutionDetails.getWeightPerformed());
        //setExecution.setResTimePerformed(setExecutionDetails.getResTimePerformed());

        workoutExecutionRepository.save(workoutExecution);
        return setExecution;
    }
    /*
    @Transactional
    // provavelmente vai ser necessário.
    public SetExecution updateSetExecution(Integer workoutExecutionId, SetExecution setExecutionDetails) {

    }*/
}
