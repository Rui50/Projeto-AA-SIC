package com.example.appfitness.DTOs.Aluno;


import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.BodyMetrics;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponseDTO {
    private Integer id;
    private String name;
    private String email;
    private Double height;
    private Double currentWeight;
    private String latestActivity;

    public static AlunoResponseDTO fromEntity(Aluno aluno, Optional<BodyMetrics> latestBodyMetrics, Optional<LocalDateTime> lastWorkoutTime) {
        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setId(aluno.getId());
        dto.setName(aluno.getName());
        dto.setEmail(aluno.getEmail());

        latestBodyMetrics.ifPresent(metrics -> {
            dto.setHeight(metrics.getHeight());
            dto.setCurrentWeight(metrics.getWeight());
        });

        dto.setLatestActivity(formatLatestActivity(lastWorkoutTime));

        return dto;
    }

    private static String formatLatestActivity(Optional<LocalDateTime> lastActivityTime) {
        if (lastActivityTime.isEmpty()) {
            return "Never";
        }

        LocalDateTime last = lastActivityTime.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return last.format(formatter);
    }
}
