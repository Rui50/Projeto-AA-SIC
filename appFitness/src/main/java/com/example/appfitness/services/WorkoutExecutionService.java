package com.example.appfitness.services;

import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private ExerciseExecutionRepository exerciseExecutionRepository;
    private SetExecutionRepository setExecutionRepository;

    public WorkoutExecutionService(WorkoutExecutionRepository workoutExecutionRepository,
                                   UserRepository userRepository,
                                   WorkoutPlanRepository workoutPlanRepository,
                                   ExerciseDataRepository exerciseDataRepository,
                                   ExerciseExecutionRepository exerciseExecutionRepository, // <-- New
                                   SetExecutionRepository setExecutionRepository) { // <-- New
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.userRepository = userRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseDataRepository = exerciseDataRepository;
        this.exerciseExecutionRepository = exerciseExecutionRepository; // <-- New
        this.setExecutionRepository = setExecutionRepository; // <-- New
    }

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
        workout.setExecutionDate(workout.getStartTime().toLocalDate());

        if (workout.getExerciseExecutions() == null ){
            workout.setExerciseExecutions(new ArrayList<>());
        }

        return workoutExecutionRepository.save(workout);
    }

    @Transactional
    public WorkoutExecution finishWorkout(Integer executionId, String feedback, WorkoutExecution.WorkoutStatus status) {
        WorkoutExecution execution = workoutExecutionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("WorkoutExecution not found with id " + executionId));

        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(status != null ? status : WorkoutExecution.WorkoutStatus.COMPLETED);
        execution.setFeeback(feedback);

        return workoutExecutionRepository.save(execution);
    }

    @Transactional
    public void deleteWorkoutExecutionById(Integer id) {
        if (!workoutExecutionRepository.existsById(id)) {
            throw new RuntimeException("Workout Execution not found: " + id);
        }
        workoutExecutionRepository.deleteById(id);
    }

    public Optional<WorkoutExecution> getWorkoutExecutionById(Integer id) {
        return workoutExecutionRepository.findById(id);
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
    public SetExecution recordSetExecution(Integer exerciseExecutionId, SetExecution setExecutionDetails) {
        // find the exercise
        ExerciseExecution exerciseExecution = exerciseExecutionRepository.findById(exerciseExecutionId)
                .orElseThrow(() -> new RuntimeException("Exercise Execution not found: " + exerciseExecutionId));


        // buscar o set, se nao existe cria novo (user decidiu fazer um set a mais)
        Optional<SetExecution> set = exerciseExecution.getPerformedSets().stream()
                .filter(s -> s.getSetNumber().equals(setExecutionDetails.getSetNumber()))
                .findFirst();

        SetExecution setExecution;

        if(set.isPresent()) {
            setExecution = set.get();
        }
        else {
            setExecution = new SetExecution();
            setExecution.setExerciseExecution(exerciseExecution);
            setExecution.setSetNumber(setExecutionDetails.getSetNumber());
            exerciseExecution.getPerformedSets().add(setExecution);
        }

        setExecution.setRepsPerformed(setExecutionDetails.getRepsPerformed());
        setExecution.setWeightPerformed(setExecutionDetails.getWeightPerformed());
        setExecution.setResTimePerformed(setExecutionDetails.getResTimePerformed());

        exerciseExecutionRepository.save(exerciseExecution);
        return setExecutionRepository.save(setExecution);
    }
    /*
    @Transactional
    // provavelmente vai ser necessário.
    public SetExecution updateSetExecution(Integer workoutExecutionId, SetExecution setExecutionDetails) {

    }*/

    public Optional<ExerciseExecution> getExerciseExecutionById(Integer id) {
        return exerciseExecutionRepository.findById(id);
    }

}
