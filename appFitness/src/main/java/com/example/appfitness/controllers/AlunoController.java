package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.Aluno.AlunoDTO;
import com.example.appfitness.DTOs.Aluno.AlunoResponseDTO;
import com.example.appfitness.DTOs.Aluno.ClientInfoResponseDTOP;
import com.example.appfitness.DTOs.Professor.ProfessorDTO;
import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.User;
import com.example.appfitness.services.AlunoService;
import com.example.appfitness.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {
    private AlunoService alunoService;
    private UserService userService;
    private AuthService authService;

    public AlunoController(AlunoService alunoService, UserService userService, AuthService authService) {
        this.alunoService = alunoService;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<AlunoDTO> getAllAlunos() {
        return alunoService.findAlunosWithoutProfessor();
    }

    @GetMapping("/professor")
    public ResponseEntity<ProfessorDTO> getProfessor(@CookieValue(value = "token", defaultValue = "") String token) {
        Integer userId = Integer.valueOf(authService.getUserIdFromToken(token));

        Optional<ProfessorDTO> professorDTO = alunoService.getAlunoProfessor(userId);

        return professorDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            System.out.println("Assigning aluno with ID: " + alunoId + " to professor with ID: " + professorId);
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

    // depois usar middleware para o id do professor e verificamos se ele é professor do aluno
    @GetMapping("/info/{id}")
    public ResponseEntity<ClientInfoResponseDTOP> getClientInfo(@PathVariable int id, @RequestParam int professorId) {
        System.out.println("Student ID: " + id);
        System.out.println("Professor ID: " + professorId);

        ClientInfoResponseDTOP clientInfoResponseDTOP = alunoService.getClientInfo(id, professorId);
        return ResponseEntity.ok(clientInfoResponseDTOP);
    }


}
