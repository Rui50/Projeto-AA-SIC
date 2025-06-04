package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.DTOs.Progress.WorkoutExecutionDTO;
import com.example.appfitness.models.WorkoutExecution;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AllWorkoutsResponseDTO {
    private List<WorkoutExecutionResponseDTO> workoutExecutions;
    private Long totalCount;
    private Integer currentPage;
    private Integer totalPages;
    private Integer pageSize;
}
