package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.SetData;
import lombok.*;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SetDataDTO {
    private Integer id;
    private Integer setNumber;
    private Integer reps;
    private Double weight;
    private Float restTimeSugested;

    public static SetDataDTO fromEntity(SetData setData) {
        SetDataDTO dto = new SetDataDTO();
        dto.setId(setData.getId());
        dto.setSetNumber(setData.getSetNumber());
        dto.setReps(setData.getRepsPlanned());
        dto.setWeight(setData.getWeightPlanned());
        dto.setRestTimeSugested(setData.getRestTimeSugested());
        return dto;
    }
    /*
    @Override
    public String toString() {
        return "SetDataDTO{" +
                "id=" + id +
                ", setNumber=" + setNumber +
                ", reps=" + reps +
                ", weight=" + weight +
                ", restTimeSugested=" + restTimeSugested +
                '}';
    }*/

}
