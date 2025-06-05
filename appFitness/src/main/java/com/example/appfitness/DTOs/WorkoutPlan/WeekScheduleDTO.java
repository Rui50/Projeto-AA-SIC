// src/main/java/com/example/appfitness/DTOs/WorkoutPlan/WeekScheduleDTO.java
package com.example.appfitness.DTOs.WorkoutPlan;
import java.util.List;
import java.util.Map;

public class WeekScheduleDTO {
    private Map<String, List<String>> schedule;

    public WeekScheduleDTO(Map<String, List<String>> schedule) {
        this.schedule = schedule;
    }

    public Map<String, List<String>> getSchedule() {
        return schedule;
    }

    public void setSchedule(Map<String, List<String>> schedule) {
        this.schedule = schedule;
    }
}
