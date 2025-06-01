package com.example.appfitness.DTOs.Aluno;

import com.example.appfitness.models.Aluno;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private Integer id;
    private String name;
    private String email;

    public static AlunoDTO fromEntity(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(aluno.getId());
        dto.setName(aluno.getName());
        dto.setEmail(aluno.getEmail());
        return dto;
    }
}
