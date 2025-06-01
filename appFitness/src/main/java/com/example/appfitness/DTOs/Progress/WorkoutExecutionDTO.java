package com.example.appfitness.DTOs.Progress;

import com.example.appfitness.models.WorkoutExecution;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkoutExecutionDTO {
    private Integer id;
    private LocalDate executionDate;
    private String workoutName;
    private Long duration;
    private String status;
    private int exercises; // number

    public static WorkoutExecutionDTO fromEntity(WorkoutExecution workoutExecution) {
        WorkoutExecutionDTO dto = new WorkoutExecutionDTO();
        dto.setId(workoutExecution.getId());
        dto.setExecutionDate(workoutExecution.getExecutionDate());
        dto.setWorkoutName(workoutExecution.getWorkoutPlan().getName());
        Long duration = 0L;
        duration = Duration.between(workoutExecution.getStartTime(), workoutExecution.getEndTime()).toMillis();
        dto.setDuration(duration);
        dto.setStatus(workoutExecution.getStatus().toString());
        dto.setExercises(workoutExecution.getExerciseExecutions().size());

        return dto;
    }
}
