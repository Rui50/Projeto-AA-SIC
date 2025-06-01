package com.example.appfitness.DTOs.Aluno;

import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanResponseDTO;
import com.example.appfitness.DTOs.bodyMetrics.BodyMetricsRequestDTO;
import com.example.appfitness.DTOs.bodyMetrics.BodyMetricsResposeDTO;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfoResponseDTOP {
    private AlunoDTO aluno;
    private Optional<BodyMetricsResposeDTO> bodyMetrics;
    private List<WorkoutPlanResponseDTO> workoutPlan;
}
