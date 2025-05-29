package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.WorkoutPlan;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Set;

@Getter
@Setter
public class WorkoutPlanRequestDTO {
    private String name;
    private String description;
    private Integer ownerId;
    private WorkoutPlan.WorkoutScheduleType scheduleType;
    private Set<DayOfWeek> scheduledDays;
    private boolean active;

    // if we decide to add the exercices when creating
    // private List<ExerciseDataRequestDTO> exercises;
}
