package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.WorkoutPlan;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class WorkoutPlanRequestDTO {
    private String name;
    private String description;
    private Integer ownerId;
    private WorkoutPlan.WorkoutScheduleType scheduleType;
    private Set<DayOfWeek> scheduledDays;
    private boolean active;

    private List<ExerciseDataRequestDTO> exercises;
    /*
    @Override
    public String toString() {
        return "WorkoutPlanRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ownerId=" + ownerId +
                ", scheduleType=" + scheduleType +
                ", scheduledDays=" + scheduledDays +
                ", active=" + active +
                ", exercises=" + exercises +
                '}';
    }*/
}
