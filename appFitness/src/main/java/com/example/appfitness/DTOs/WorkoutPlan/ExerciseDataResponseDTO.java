package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.DTOs.Exercise.ExerciseResponseDTO;
import com.example.appfitness.models.Exercise;
import com.example.appfitness.models.ExerciseData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

// used for details about an exercise part of a workout plan, includes the sets

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDataResponseDTO {
    private int id;
    private ExerciseResponseDTO exercise;
    private String note;
    private List<SetDataDTO> plannedSets;

    public static ExerciseDataResponseDTO fromEntity(ExerciseData exerciseData) {
        ExerciseDataResponseDTO  dto = new ExerciseDataResponseDTO ();
        dto.setId(exerciseData.getId());
        dto.setNote(exerciseData.getNote());

        // map the exercise to the DTO
        if (exerciseData.getExercise() != null) {
            dto.setExercise(ExerciseResponseDTO.fromEntity(exerciseData.getExercise()));
        }

        if (exerciseData.getPlannedSets() != null) {
            dto.setPlannedSets(exerciseData.getPlannedSets().stream()
                    .map(SetDataDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
