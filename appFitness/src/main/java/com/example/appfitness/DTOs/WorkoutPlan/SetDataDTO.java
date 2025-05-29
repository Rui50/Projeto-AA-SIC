package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.SetData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SetDataDTO {
    private Integer id;
    private Integer setNumber;
    private Integer reps;
    private Double weight;
    private Duration restTimeSugested;

    public static SetDataDTO fromEntity(SetData setData) {
        SetDataDTO dto = new SetDataDTO();
        dto.setId(setData.getId());
        dto.setSetNumber(setData.getSetNumber());
        dto.setReps(setData.getRepsPlanned());
        dto.setWeight(setData.getWeightPlanned());
        dto.setRestTimeSugested(setData.getRestTimeSuggestion());
        return dto;
    }
}
