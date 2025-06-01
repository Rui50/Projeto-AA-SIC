package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.Aluno.AlunoDTO;
import com.example.appfitness.DTOs.Aluno.AlunoResponseDTO;
import com.example.appfitness.models.Aluno;
import com.example.appfitness.services.AlunoService;
import com.example.appfitness.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {
    private AlunoService alunoService;
    private UserService userService;

    public AlunoController(AlunoService alunoService, UserService userService) {
        this.alunoService = alunoService;
        this.userService = userService;
    }

    @GetMapping
    public List<AlunoDTO> getAllAlunos() {
        return alunoService.getAllAlunos();
    }

    @GetMapping("/{id}")
    public List<AlunoResponseDTO> getAlunosFromProfessor(@PathVariable int id) {
        return alunoService.getAlunosByProfessor(id);
    }

    // resolver os problemas de segurança aqui,
    // nao devia ser preciso enviar o id do professor
    @PostMapping("/{alunoId}/assign/{professorId}")
    public ResponseEntity<Void> assignAlunoToProfessor(@PathVariable int alunoId, @PathVariable int professorId) {
        try{
            alunoService.assignProfessorToAluno(alunoId, professorId);
            return ResponseEntity.status(200).build();
        }
        catch (RuntimeException  e){
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{alunoId}/unassign")
    public void unassignAluno(@PathVariable int alunoId) {
        alunoService.unassignProfessorFromAluno(alunoId);

    }

}
