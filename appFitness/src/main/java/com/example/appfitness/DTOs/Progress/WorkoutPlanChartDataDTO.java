package com.example.appfitness.DTOs.Progress;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkoutPlanChartDataDTO {
    private Integer id;
    private String name;
    private List<ChartDataDTO> volumeChartData;
}
