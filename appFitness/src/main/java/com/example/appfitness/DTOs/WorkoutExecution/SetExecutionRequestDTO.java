package com.example.appfitness.DTOs.WorkoutExecution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetExecutionRequestDTO {
    private Integer setNumber;
    private Integer repsPerformed;
    private Double weightPerformed;
    private Duration restTimePerformed;
}
