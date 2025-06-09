package com.example.appfitness.services;

import com.example.appfitness.DTOs.Aluno.AlunoDTO;
import com.example.appfitness.DTOs.Aluno.AlunoResponseDTO;
import com.example.appfitness.DTOs.Aluno.ClientInfoResponseDTOP;
import com.example.appfitness.DTOs.Professor.ProfessorDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanResponseDTO;
import com.example.appfitness.DTOs.bodyMetrics.BodyMetricsResposeDTO;
import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class AlunoService {
    private final UserService userService;
    private AlunoRepository alunoRepository;
    private ProfessorRepository professorRepository;
    private BodyMetricsRepository bodyMetricsRepository;
    private WorkoutExecutionRepository workoutExecutionRepository;
    private WorkoutPlanRepository workoutPlanRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private NotificationService notificationService;

    public AlunoService(AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository,
                        BodyMetricsRepository bodyMetricsRepository,
                        WorkoutExecutionRepository workoutExecutionRepository,
                        UserService userService,
                        WorkoutPlanRepository workoutPlanRepository,
                        NotificationService notificationService) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.bodyMetricsRepository = bodyMetricsRepository;
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.userService = userService;
        this.workoutPlanRepository = workoutPlanRepository;
        this.notificationService = notificationService;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Optional<ProfessorDTO> getAlunoProfessor(Integer id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);

        if (aluno.isPresent()) {
            Aluno al = aluno.get();
            if(al.getProfessor() != null) {
                ProfessorDTO professorDTO = new ProfessorDTO();
                professorDTO.setId(al.getProfessor().getId());
                professorDTO.setName(al.getProfessor().getName());
                professorDTO.setEmail(al.getProfessor().getEmail());

                return Optional.of(professorDTO);
            }
        }
        return Optional.empty();
    }

    public List<AlunoDTO> findAlunosWithoutProfessor() {
       // return alunoRepository.findAlunosWithoutProfessor();
        return alunoRepository.findAlunosWithoutProfessor().stream()
                .map(AlunoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Aluno assignProfessorToAluno(Integer alunoId, Integer professorId) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno not found " + alunoId));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor not found " + professorId));
        
        aluno.setProfessor(professor);
        System.out.println("Assigning aluno with ID: " + alunoId + " to professor with ID: " + professorId);

        String message = String.format("Foi te atribuido o professor '%s'.", professor.getName());
        notificationService.createNotification(
            alunoId,
            message,
            Notification.NotificationType.PROFESSOR_ASSIGNED
        );
        

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
                    Optional<WorkoutExecution> latestWorkoutExecution = workoutExecutionRepository.
                            findTopByUserIdAndStatusOrderByStartTimeDesc(aluno.getId(), WorkoutExecution.WorkoutStatus.COMPLETED);
                    Optional<LocalDateTime> lastWorkoutTime = latestWorkoutExecution.map(WorkoutExecution::getStartTime);

                    return AlunoResponseDTO.fromEntity(aluno, latestBM, lastWorkoutTime);
                }).collect(Collectors.toList());
    }

    public ClientInfoResponseDTOP getClientInfo(Integer id, Integer professorId) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno not found " + id));

        AlunoDTO clientDetails = AlunoDTO.fromEntity(aluno);

        Optional<BodyMetrics> latestBodyMetrics = bodyMetricsRepository.findFirstByUser_IdOrderByUpdatedAtDesc(aluno.getId());
        Optional<BodyMetricsResposeDTO> latestBodyMetricDTO = latestBodyMetrics.map(BodyMetricsResposeDTO::fromEntity);

        List<WorkoutPlan> workoutPlans = workoutPlanRepository.findByCreatedByAndOwnerIdOrderByUpdatedAtDesc(professorId, id);

        List<WorkoutPlanResponseDTO> workoutPlansDTOs = workoutPlans.stream()
                .map(workoutPlan -> {
                    AtomicReference<String> createdByUsername = new AtomicReference<>("");
                    userService.getUserById(workoutPlan.getCreatedBy())
                            .ifPresent(user -> createdByUsername.set(user.getName()));
                    return WorkoutPlanResponseDTO.fromEntity(workoutPlan, createdByUsername.get());
                }).toList();

        return new ClientInfoResponseDTOP(clientDetails, latestBodyMetricDTO, workoutPlansDTOs);
    }

    public Aluno saveAluno(Aluno user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return alunoRepository.save(user);
    }
}

