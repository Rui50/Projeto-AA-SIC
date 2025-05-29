package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.WorkoutPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanResponseDTO {
    private int id;
    private String name;
    private String description;
    private Integer createdBy; // creator id
    private LocalTime updatedAt;
    private boolean active;
    private WorkoutPlan.WorkoutScheduleType scheduleType;
    private Integer ownerId;
    private List<ExerciseDataResponseDTO> exercises;
    private Set<DayOfWeek> scheduledDays;

    public static WorkoutPlanResponseDTO fromEntity(WorkoutPlan workoutPlan) {
        WorkoutPlanResponseDTO dto = new WorkoutPlanResponseDTO();
        dto.setId(workoutPlan.getId());
        dto.setName(workoutPlan.getName());
        dto.setDescription(workoutPlan.getDescription());
        dto.setCreatedBy(workoutPlan.getCreatedBy());
        dto.setUpdatedAt(workoutPlan.getUpdatedAt());
        dto.setActive(workoutPlan.isActive());
        dto.setScheduleType(workoutPlan.getScheduleType());
        dto.setOwnerId(workoutPlan.getOwner().getId());

        // Map exercises to ExerciseDataResponseDTOs
        if (workoutPlan.getExercises() != null) {
            dto.setExercises(workoutPlan.getExercises().stream()
                    .map(ExerciseDataResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        dto.setScheduledDays(workoutPlan.getScheduledDays());
        return dto;
    }
}
