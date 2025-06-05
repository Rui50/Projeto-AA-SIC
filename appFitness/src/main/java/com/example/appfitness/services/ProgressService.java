package com.example.appfitness.services;

import com.example.appfitness.DTOs.Progress.*;
import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.ExerciseExecution;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.repositories.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {
    private final ExerciseExecutionRepository exerciseExecutionRepository;
    private WorkoutExecutionRepository workoutExecutionRepository;
    private SetExecutionRepository setExecutionRepository;
    private UserRepository userRepository;
    private BodyMetricsRepository bodyMetricsRepository;

    public ProgressService(WorkoutExecutionRepository workoutExecutionRepository, SetExecutionRepository setExecutionRepository, UserRepository userRepository,
                           BodyMetricsRepository bodyMetricsRepository, ExerciseExecutionRepository exerciseExecutionRepository) {
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.setExecutionRepository = setExecutionRepository;
        this.userRepository = userRepository;
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.exerciseExecutionRepository = exerciseExecutionRepository;
    }

    public ProgressDashboardDTO getProgress(ProgressRequestDTO progressRequestDTO) {
        ProgressStatsDTO stats = getProgressStats(progressRequestDTO);
        List<WorkoutExecutionDTO> recentWorkouts = getRecentWorkouts(progressRequestDTO);
        List<WorkoutPlanChartDataDTO> volumePerWorkout = getVolumePerWorkout(progressRequestDTO);

        return new ProgressDashboardDTO(stats, recentWorkouts, volumePerWorkout);
    }

    public ProgressStatsDTO getProgressStats(ProgressRequestDTO progressRequestDTO) {
        Integer userId = progressRequestDTO.getUserId();
        String timePeriod = progressRequestDTO.getTimePeriod();
        LocalDateTime startDate = getStartDateTime(timePeriod);
        LocalDateTime endDate = LocalDateTime.now();

        Long workoutsCompleted = workoutExecutionRepository.countByUserIdAndStatusAndStartTimeBetween(
                userId, WorkoutExecution.WorkoutStatus.COMPLETED, startDate, endDate
        );

        Double totalWeightLifted = setExecutionRepository.calculateTotalVolumeForUserAndDateRange(
                userId, WorkoutExecution.WorkoutStatus.COMPLETED, startDate, endDate
        );

        Double currentBodyWeight = 0.00;
        Double bodyWeightChange = null;

        Optional<BodyMetrics> latestWeightEntry = bodyMetricsRepository.findFirstByUser_IdOrderByUpdatedAtDesc(userId);

        if (latestWeightEntry.isPresent()) {
            currentBodyWeight = latestWeightEntry.get().getWeight();
        }

        if(!"all".equalsIgnoreCase(timePeriod) && currentBodyWeight > 0) {
            LocalDateTime bodyMetricsCompareStartDate = getStartDateTime(timePeriod);//.toLocalDate();
            LocalDateTime bodyMetricsCompareEndDate = LocalDateTime.now();

            List<BodyMetrics> bmInSelectedPeriod = bodyMetricsRepository.findByUser_IdAndUpdatedAtBetweenOrderByUpdatedAtAsc(userId, bodyMetricsCompareStartDate, bodyMetricsCompareEndDate);

            if(bmInSelectedPeriod != null && bmInSelectedPeriod.size() >= 2 ) {
                //double startWeight = closestBodyWeight.get().getWeight();
                double startWeight = bmInSelectedPeriod.getFirst().getWeight();

                bodyWeightChange = currentBodyWeight - startWeight;
            }
        }

        ProgressStatsDTO progressStatsDTO = new ProgressStatsDTO();
        progressStatsDTO.setWorkoutsCompleted(workoutsCompleted);
        progressStatsDTO.setCurrentBodyWeight(currentBodyWeight);
        if (bodyWeightChange != null) {
            progressStatsDTO.setBodyWeightChange(bodyWeightChange);
        }

        progressStatsDTO.setTotalWeightLifted(totalWeightLifted);
        return progressStatsDTO;
    }

    public List<WorkoutExecutionDTO> getRecentWorkouts(ProgressRequestDTO progressRequestDTO) {
        Integer userId = progressRequestDTO.getUserId();
        List<WorkoutExecution> allCompletedWorkouts = workoutExecutionRepository.findTopByUserIdOrderByStartTimeDesc(userId);

        return allCompletedWorkouts.stream()
                .filter(we -> we.getStatus() == WorkoutExecution.WorkoutStatus.COMPLETED)
                .limit(5)
                .map(WorkoutExecutionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WorkoutPlanChartDataDTO> getVolumePerWorkout(ProgressRequestDTO progressRequestDTO) {
        Integer userId = progressRequestDTO.getUserId();
        LocalDate startDate = getStartDateAsLocalDate(progressRequestDTO.getTimePeriod());
        LocalDate endDate = LocalDate.now();


        // Começamos por obter os ids das workoutExecutions de um workout plan
        List<WorkoutExecution> completedWorkouts = workoutExecutionRepository.findCompletedWorkoutExecutionsWithWorkoutPlan(
                userId,
                WorkoutExecution.WorkoutStatus.COMPLETED,
                startDate,
                endDate
        );

        Set<Integer> completedWorkoutIds = completedWorkouts.stream().map(WorkoutExecution::getId).collect(Collectors.toSet());

        if (completedWorkoutIds.isEmpty()){
            return new ArrayList<>();
        }

        // pegamos nas execiseExecutions
        List<ExerciseExecution> exerciseExecutions = exerciseExecutionRepository.findByWorkoutExecutionIdsWithExerciseData(
                new ArrayList<>(completedWorkoutIds)
        );

        // mapa de Workout execution -> exercise execution

        Map<Integer, List<ExerciseExecution>> exercisesByWorkoutId = exerciseExecutions.stream()
                .collect(Collectors.groupingBy(ee -> ee.getWorkoutExecution().getId()));


        // obter os exerciseEx ids
        Set<Integer> exerciseExecutionIds = exerciseExecutions.stream()
                .map(ExerciseExecution::getId)
                .collect(Collectors.toSet());

        if (exerciseExecutionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // pegar nas set executions

        List<SetExecution> setExecutions = setExecutionRepository.findByExerciseExecutionIds(
                new ArrayList<>(exerciseExecutionIds)
        );

        // map exercise execution id -> set executions
        Map<Integer, List<SetExecution>> setsByExerciseId = setExecutions.stream()
                .collect(Collectors.groupingBy(ses -> ses.getExerciseExecution().getId()));

        // onde vai guardar os dados
        Map<Integer, WorkoutPlanChartDataDTO> workoutPlanChartDataMap = new LinkedHashMap<>();

        for (WorkoutExecution workoutExecution : completedWorkouts) {
            Integer workoutPlanId = workoutExecution.getWorkoutPlan().getId();
            String workoutPlanName = workoutExecution.getWorkoutPlan().getName();
            LocalDate executionDate = workoutExecution.getExecutionDate();

            WorkoutPlanChartDataDTO currentWorkoutPlanData = workoutPlanChartDataMap.computeIfAbsent(
                    workoutPlanId,
                    id -> new WorkoutPlanChartDataDTO(id, workoutPlanName, new ArrayList<>())
            );

            double currentExecutionVolume = 0.0;

            List<ExerciseExecution> exercisesForWorkout = exercisesByWorkoutId.getOrDefault(workoutExecution.getId(), new ArrayList<>());

            for (ExerciseExecution exerciseExecution : exercisesForWorkout) {
                // sets deste exercicio
                List<SetExecution> setsForExercise = setsByExerciseId.getOrDefault(exerciseExecution.getId(), new ArrayList<>());

                for (SetExecution setExecution : setsForExercise) {
                    if (setExecution.getWeightPerformed() != null && setExecution.getRepsPerformed() != null) {
                        currentExecutionVolume += setExecution.getWeightPerformed() * setExecution.getRepsPerformed();
                    }
                }
            }

            currentWorkoutPlanData.getVolumeChartData().add(new ChartDataDTO(executionDate, currentExecutionVolume));
        }

        for (WorkoutPlanChartDataDTO workoutPlanData : workoutPlanChartDataMap.values()) {
            workoutPlanData.getVolumeChartData().sort(Comparator.comparing(ChartDataDTO::getDate));
        }

        return new ArrayList<>(workoutPlanChartDataMap.values());

    }

    // é preciso este porque workoutExecution date é localdate e nao LocalDateTime
    private LocalDate getStartDateAsLocalDate(String timePeriod) {
        LocalDate now = LocalDate.now();

        return switch (timePeriod.toLowerCase()) {
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            case "year" -> now.minusYears(1);
            default -> LocalDate.MIN;
        };
    }

    private LocalDateTime getStartDateTime(String timePeriod) {
        LocalDateTime now = LocalDateTime.now();

        return switch (timePeriod.toLowerCase()) {
            case "week" -> now.minusWeeks(1).with(LocalTime.MIN);
            case "month" -> now.minusMonths(1).with(LocalTime.MIN);
            case "year" -> now.minusYears(1).with(LocalTime.MIN);
            default -> LocalDateTime.MIN;
        };
    }

}
