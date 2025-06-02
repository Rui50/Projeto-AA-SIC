package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.models.WorkoutExecution;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// used to return the full details of a workout exec
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkoutExecutionResponseDTO {
    private Integer id;
    private Integer userId;
    private Integer workoutPlanId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate executionDate;
    private String workoutName;
    private String status;
    private String feedback;
    private List<ExerciseExecutionResponseDTO> exerciseExecutions;

    public static WorkoutExecutionResponseDTO fromEntity(WorkoutExecution workoutExecution) {
        WorkoutExecutionResponseDTO dto = new WorkoutExecutionResponseDTO();
        dto.setId(workoutExecution.getId());
        dto.setStartTime(workoutExecution.getStartTime());
        dto.setEndTime(workoutExecution.getEndTime());
        dto.setExecutionDate(workoutExecution.getExecutionDate());
        dto.setStatus(workoutExecution.getStatus().name());
        dto.setFeedback(workoutExecution.getFeeback());
        dto.setWorkoutName(workoutExecution.getWorkoutPlan().getName());

        // verificaçoes por causa de erros temp
        if (workoutExecution.getUser() != null) {
            dto.setUserId(workoutExecution.getUser().getId());
        }
        if (workoutExecution.getWorkoutPlan() != null) {
            dto.setWorkoutPlanId(workoutExecution.getWorkoutPlan().getId());
        }

        if (workoutExecution.getExerciseExecutions() != null) {
            dto.setExerciseExecutions(workoutExecution.getExerciseExecutions().stream()
                    .map(ExerciseExecutionResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
