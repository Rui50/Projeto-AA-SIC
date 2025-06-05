package com.example.appfitness.DTOs.Settings;

import com.example.appfitness.models.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {
    private User.MetricType metricType;
}
