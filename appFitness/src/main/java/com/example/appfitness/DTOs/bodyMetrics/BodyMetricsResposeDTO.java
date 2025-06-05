package com.example.appfitness.DTOs.bodyMetrics;

import com.example.appfitness.models.BodyMetrics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsResposeDTO {
    private int id;
    private Double height;
    private Double weight;
    private Double bmi;
    private Double bodyFatPercentage;
    private LocalDateTime updatedAt;
    //private BodyMetrics.MetricType metricType;
    private Integer userId;


    public static BodyMetricsResposeDTO fromEntity(BodyMetrics bodyMetrics) {
        BodyMetricsResposeDTO dto = new BodyMetricsResposeDTO();
        dto.setId(bodyMetrics.getId());
        dto.setHeight(bodyMetrics.getHeight());
        dto.setWeight(bodyMetrics.getWeight());
        dto.setBodyFatPercentage(bodyMetrics.getBodyFatPercentage());
        dto.setBmi(bodyMetrics.getBmi());
        dto.setUpdatedAt(bodyMetrics.getUpdatedAt());
        dto.setUserId(bodyMetrics.getUser().getId());
        return dto;
    }

}
