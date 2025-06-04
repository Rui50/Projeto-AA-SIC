package com.example.appfitness.DTOs.Aluno;

import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.models.WorkoutPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardWorkoutDTO {
    public enum WorkoutStatusType {
        IN_PROGRESS,
        SCHEDULED,
        NONE
    }

    private WorkoutStatusType status;

    private String workoutName;
    private Integer exerciseCount;

    // se inprogresss
    private Integer workoutExecutionid;
    private LocalDateTime startTime;

    // se scheduled
    private Integer workoutPlanId;
    private LocalDate nextScheduledWorkout;

    public static DashboardWorkoutDTO convertToExecution(WorkoutExecution workoutExecution) {
        DashboardWorkoutDTO dto = new DashboardWorkoutDTO();
        dto.setStatus(WorkoutStatusType.IN_PROGRESS);
        dto.setWorkoutExecutionid(workoutExecution.getId());
        dto.setWorkoutName(workoutExecution.getWorkoutPlan().getName());
        dto.setExerciseCount(workoutExecution.getExerciseExecutions().size()+1);
        dto.setStartTime(workoutExecution.getStartTime());

        return dto;
    }

    public static DashboardWorkoutDTO converToScheduled(WorkoutPlan plan, LocalDate nextDate) {
        DashboardWorkoutDTO dto = new DashboardWorkoutDTO();
        dto.setStatus(WorkoutStatusType.SCHEDULED);
        dto.setWorkoutPlanId(plan.getId());
        dto.setNextScheduledWorkout(nextDate);
        dto.setWorkoutName(plan.getName());
        dto.setExerciseCount(plan.getExercises().size()+1);

        return dto;
    }

    public static DashboardWorkoutDTO none() {
        DashboardWorkoutDTO dto = new DashboardWorkoutDTO();
        dto.setStatus(WorkoutStatusType.NONE);
        return dto;
    }
}
