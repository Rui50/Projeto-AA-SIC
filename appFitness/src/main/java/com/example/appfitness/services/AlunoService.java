package com.example.appfitness.services;

import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.Professor;
import com.example.appfitness.repositories.AlunoRepository;
import com.example.appfitness.repositories.ProfessorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AlunoService(AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public Aluno salvar(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public Optional<Aluno> findAlunoById(Integer id) {
        return alunoRepository.findById(id);
    }

    // para a associação aluno professor do lado do professor
    public List<Aluno> findAllAlunos(){
        return alunoRepository.findAll();
    }

    public Optional<Aluno> findAlunoByEmail(String email) {
        return alunoRepository.findByEmail(email);
    }

    public Optional<List<Aluno>> findAlunosByProfessorId(Integer professorId) {
        return alunoRepository.findByProfessor(professorId);
    }

    public List<Aluno> findAlunosWithoutProfessor() {
        return alunoRepository.findAlunosWithoutProfessor();
    }

    @Transactional
    public Aluno assignProfessorToAluno(Integer professorId, Integer alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno not found " + alunoId));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found " + professorId));

        aluno.setProfessor(professor);
        return alunoRepository.save(aluno);
    }
}

