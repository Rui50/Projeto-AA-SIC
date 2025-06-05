package com.example.appfitness.DTOs.Progress;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProgressStatsDTO {
    private Long workoutsCompleted;
    private Double totalWeightLifted;
    private Double currentBodyWeight;
    private double bodyWeightChange;
}
