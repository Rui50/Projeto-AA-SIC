package com.example.appfitness.DTOs.Progress;

import com.example.appfitness.models.WorkoutPlan;
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

    //private List<ChartDataDTO> bodyWeightChart;
    private List<WorkoutPlanChartDataDTO> volumePerWorkout;
    //private List<ChartDataDTO> workoutsCompletedChart;
}
