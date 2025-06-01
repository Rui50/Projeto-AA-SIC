package com.example.appfitness.services;

import com.example.appfitness.DTOs.Aluno.AlunoDTO;
import com.example.appfitness.DTOs.Aluno.AlunoResponseDTO;
import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.Professor;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.repositories.AlunoRepository;
import com.example.appfitness.repositories.BodyMetricsRepository;
import com.example.appfitness.repositories.ProfessorRepository;
import com.example.appfitness.repositories.WorkoutExecutionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlunoService {
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;
    private BodyMetricsRepository bodyMetricsRepository;
    private WorkoutExecutionRepository workoutExecutionRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository
    , BodyMetricsRepository bodyMetricsRepository,
                        WorkoutExecutionRepository workoutExecutionRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.workoutExecutionRepository = workoutExecutionRepository;
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

    // resolver depois os problemas de seguraça, para já fica assim
    @Transactional
    public Aluno unassignProfessorFromAluno(Integer alunoId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno not found " + alunoId));

        /*if (aluno.getProfessor().getId() != professorId) {
            throw new RuntimeException("Professor not found " + professorId);
        }*/

        aluno.setProfessor(null);
        return alunoRepository.save(aluno);
    }

    public List<AlunoResponseDTO> getAlunosByProfessor(Integer professorId) {
        Optional<List<Aluno>> alunos = alunoRepository.findByProfessor(professorId);

        if (alunos.isEmpty()) { return List.of(); }

        return alunos.get().stream()
                .map(aluno -> {
                    Optional<BodyMetrics> latestBM = bodyMetricsRepository.findFirstByUser_IdOrderByUpdatedAtDesc(aluno.getId());
                    Optional<WorkoutExecution> latestWorkoutExecution = workoutExecutionRepository.findTopByUserIdAndStatusOrderByStartTimeDesc(aluno.getId(), WorkoutExecution.WorkoutStatus.COMPLETED);
                    Optional<LocalDateTime> lastWorkoutTime = latestWorkoutExecution.map(WorkoutExecution::getStartTime);

                    return AlunoResponseDTO.fromEntity(aluno, latestBM, lastWorkoutTime);
                }).collect(Collectors.toList());
    }

    public List<AlunoDTO> getAllAlunos() {
        return alunoRepository.findAll().stream()
                .map(AlunoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}

