package com.example.appfitness.DTOs.Progress;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProgressRequestDTO {
    private String timePeriod;
    private Integer userId;
}