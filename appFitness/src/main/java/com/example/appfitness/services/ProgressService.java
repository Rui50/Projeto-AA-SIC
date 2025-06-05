package com.example.appfitness.services;

import com.example.appfitness.DTOs.Progress.ProgressDashboardDTO;
import com.example.appfitness.DTOs.Progress.ProgressRequestDTO;
import com.example.appfitness.DTOs.Progress.ProgressStatsDTO;
import com.example.appfitness.DTOs.Progress.WorkoutExecutionDTO;
import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.repositories.BodyMetricsRepository;
import com.example.appfitness.repositories.SetExecutionRepository;
import com.example.appfitness.repositories.UserRepository;
import com.example.appfitness.repositories.WorkoutExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {
    private WorkoutExecutionRepository workoutExecutionRepository;
    private SetExecutionRepository setExecutionRepository;
    private UserRepository userRepository;
    private BodyMetricsRepository bodyMetricsRepository;

    public ProgressService(WorkoutExecutionRepository workoutExecutionRepository, SetExecutionRepository setExecutionRepository, UserRepository userRepository,
                           BodyMetricsRepository bodyMetricsRepository) {
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.setExecutionRepository = setExecutionRepository;
        this.userRepository = userRepository;
        this.bodyMetricsRepository = bodyMetricsRepository;
    }

    public ProgressDashboardDTO getProgress(ProgressRequestDTO progressRequestDTO) {
        ProgressStatsDTO stats = getProgressStats(progressRequestDTO);
        List<WorkoutExecutionDTO> recentWorkouts = getRecentWorkouts(progressRequestDTO);

        return new ProgressDashboardDTO(stats, recentWorkouts);
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
