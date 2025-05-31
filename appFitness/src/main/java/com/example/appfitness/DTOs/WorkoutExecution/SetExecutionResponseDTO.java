package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.models.SetExecution;
import lombok.*;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SetExecutionResponseDTO {
    private Integer id;
    private Integer setNumber;
    private Integer repsPerformed;
    private Double weightPerformed;
    private Duration restTimePerformed;
    private Integer plannedSetId;
    private boolean completed;

    public static SetExecutionResponseDTO fromEntity(SetExecution setExecution) {
        SetExecutionResponseDTO dto = new SetExecutionResponseDTO();
        dto.setId(setExecution.getId());
        dto.setSetNumber(setExecution.getSetNumber());
        dto.setRepsPerformed(setExecution.getRepsPerformed());
        dto.setWeightPerformed(setExecution.getWeightPerformed());
        dto.setRestTimePerformed(setExecution.getResTimePerformed());
        if (setExecution.getPlannedSet() != null) {
            dto.setPlannedSetId(setExecution.getPlannedSet().getId());
        }
        dto.setCompleted(setExecution.isCompleted());
        return dto;
    }
}
