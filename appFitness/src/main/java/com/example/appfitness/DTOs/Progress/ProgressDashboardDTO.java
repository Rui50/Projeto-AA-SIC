package com.example.appfitness.DTOs.Progress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDashboardDTO {
    private ProgressStatsDTO progress;
    private List<WorkoutExecutionDTO> recentWorkouts;
}
