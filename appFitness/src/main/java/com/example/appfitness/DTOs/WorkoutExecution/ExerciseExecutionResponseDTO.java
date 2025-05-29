package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.DTOs.WorkoutPlan.ExerciseDataResponseDTO;
import com.example.appfitness.models.ExerciseExecution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseExecutionResponseDTO {
    private Integer id;
    private ExerciseDataResponseDTO exerciseData;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private List<SetExecutionResponseDTO> performedSets;

    public static ExerciseExecutionResponseDTO fromEntity(ExerciseExecution exerciseExecution) {
        ExerciseExecutionResponseDTO dto = new ExerciseExecutionResponseDTO();
        dto.setId(exerciseExecution.getId());
        dto.setStartTime(exerciseExecution.getStartTime());
        dto.setEndTime(exerciseExecution.getEndTime());
        dto.setStatus(exerciseExecution.getStatus().name()); // converter enum para string

        if (exerciseExecution.getExerciseData() != null) {
            dto.setExerciseData(ExerciseDataResponseDTO.fromEntity(exerciseExecution.getExerciseData()));
        }

        if (exerciseExecution.getPerformedSets() != null) {
            dto.setPerformedSets(exerciseExecution.getPerformedSets().stream()
                    .map(SetExecutionResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
