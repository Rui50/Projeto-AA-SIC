package com.example.appfitness.DTOs.WorkoutExecution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
// basicamente o de SetExecutionRequestDTO
public class PreviousPerformedDTO {
    private Integer setNumber;
    private Integer previousRepsPerformed;
    private Double previousWeightPerformed;
}
