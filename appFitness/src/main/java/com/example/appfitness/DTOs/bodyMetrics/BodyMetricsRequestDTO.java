package com.example.appfitness.DTOs.bodyMetrics;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsRequestDTO {
    private Integer userId;
    private Double height;
    private Double weight;
    private Double bodyFatPercentage;
    // bmi, updatedAT, in other

}
