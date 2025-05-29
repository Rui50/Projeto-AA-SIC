package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.models.SetExecution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetExecutionResponseDTO {
    private Integer id;
    private Integer setNumber;
    private Integer repsPerformed;
    private Double weightPerformed;
    private Duration restTimePerformed;

    public static SetExecutionResponseDTO fromEntity(SetExecution setExecution) {
        SetExecutionResponseDTO dto = new SetExecutionResponseDTO();
        dto.setId(setExecution.getId());
        dto.setSetNumber(setExecution.getSetNumber());
        dto.setRepsPerformed(setExecution.getRepsPerformed());
        dto.setWeightPerformed(setExecution.getWeightPerformed());
        dto.setRestTimePerformed(setExecution.getResTimePerformed());
        return dto;
    }
}
