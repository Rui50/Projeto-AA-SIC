package com.example.appfitness.services;

import com.example.appfitness.DTOs.WorkoutExecution.AllWorkoutsRequestDTO;
import com.example.appfitness.DTOs.WorkoutExecution.AllWorkoutsResponseDTO;
import com.example.appfitness.DTOs.WorkoutExecution.WorkoutExecutionResponseDTO;
import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
import jakarta.transaction.Transactional;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responável por gerencial WOrkoutexecution, ExerciseExecution e SetExecution
 */
@Service
public class WorkoutExecutionService {
    private WorkoutExecutionRepository workoutExecutionRepository;
    private UserRepository userRepository;
    private WorkoutPlanRepository workoutPlanRepository;
    private ExerciseDataRepository exerciseDataRepository;
    private ExerciseExecutionRepository exerciseExecutionRepository;
    private SetExecutionRepository setExecutionRepository;
    private SetDataRepository setDataRepository;

    public WorkoutExecutionService(WorkoutExecutionRepository workoutExecutionRepository,
                                   UserRepository userRepository,
                                   WorkoutPlanRepository workoutPlanRepository,
                                   ExerciseDataRepository exerciseDataRepository,
                                   ExerciseExecutionRepository exerciseExecutionRepository,
                                   SetExecutionRepository setExecutionRepository,
                                   SetDataRepository setDataRepository) {
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.userRepository = userRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseDataRepository = exerciseDataRepository;
        this.exerciseExecutionRepository = exerciseExecutionRepository;
        this.setExecutionRepository = setExecutionRepository;
        this.setDataRepository = setDataRepository;
    }

    @Transactional
    public WorkoutExecution startWorkout(Integer userId, Integer workoutPlanId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found" + userId));
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found " + workoutPlanId));

        // primeiro verificar se o utilizador já esta a meio de um treino
        Optional<WorkoutExecution> onGoingWorkout = workoutExecutionRepository.findByUserAndStatus(user, WorkoutExecution.WorkoutStatus.IN_PROGRESS);

        if(onGoingWorkout.isPresent()) {
            throw new RuntimeException("Theres an ongoing workout");
        }

        WorkoutExecution workout = new WorkoutExecution();

        workout.setUser(user);
        workout.setWorkoutPlan(workoutPlan);
        workout.setStartTime(LocalDateTime.now());
        workout.setStatus(WorkoutExecution.WorkoutStatus.IN_PROGRESS);
        workout.setExecutionDate(workout.getStartTime().toLocalDate());

        if (workout.getExerciseExecutions() == null ){
            workout.setExerciseExecutions(new ArrayList<>());
        }

        if (workoutPlan.getExercises() != null && !workoutPlan.getExercises().isEmpty()) {
            for (ExerciseData plannedExerciseData : workoutPlan.getExercises()) {
                ExerciseExecution exerciseExecution = new ExerciseExecution();
                exerciseExecution.setWorkoutExecution(workout);
                exerciseExecution.setExerciseData(plannedExerciseData);
                exerciseExecution.setStatus(ExerciseExecution.Status.NOT_STARTED);
                exerciseExecution.setPerformedSets(new ArrayList<>());

                workout.getExerciseExecutions().add(exerciseExecution);
            }
        } else {
            System.out.println("Warning: Workout Plan ID " + workoutPlanId + " has no associated ExerciseData entries.");
        }

        return workoutExecutionRepository.save(workout);
    }

    @Transactional
    public WorkoutExecution finishWorkout(Integer executionId, String feedback, WorkoutExecution.WorkoutStatus status) {
        WorkoutExecution execution = workoutExecutionRepository.findById(executionId)
                .orElseThrow(() -> new RuntimeException("WorkoutExecution not found with id " + executionId));

        execution.setEndTime(LocalDateTime.now());
        execution.setStatus(status != null ? status : WorkoutExecution.WorkoutStatus.COMPLETED);
        execution.setFeeback(feedback);

        return workoutExecutionRepository.save(execution);
    }

    // prob not needed
    @Transactional
    public void deleteWorkoutExecutionById(Integer id) {
        if (!workoutExecutionRepository.existsById(id)) {
            throw new RuntimeException("Workout Execution not found: " + id);
        }
        workoutExecutionRepository.deleteById(id);
    }

    public Optional<WorkoutExecution> getWorkoutExecutionById(Integer id) {
        return workoutExecutionRepository.findById(id);
    }

    // https://www.baeldung.com/spring-data-jpa-pagination-sorting
    // https://ardijorganxhi.medium.com/implement-pagination-at-your-spring-boot-application-a540270b5f60

    public AllWorkoutsResponseDTO getAllWorkoutsForUser(AllWorkoutsRequestDTO request){
        Integer userId = request.getUserID();

        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit() : 10;

        String searchQuery = request.getSearchQuery();
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        String sortBy = request.getSortBy();
        String sortOrder = request.getSortOrder();

        // executionDate only for now
        String sortByDef = "executionDate";
        if (sortBy != null && !sortBy.isEmpty()) {
            sortByDef = sortBy;
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if(sortOrder.equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }



        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found" + userId));

        Page<WorkoutExecution> workoutPage;

        // caso 1: tem search query
        if(searchQuery != null && !searchQuery.isEmpty()) {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, "workoutPlan.name"));

            // se tambem tem datas
            if(startDate != null && endDate != null) {
                workoutPage = workoutExecutionRepository.findWorkoutsByUserAndFilters(
                        user, searchQuery, startDate, endDate, pageable);
            }
            // se nao inclui datas
            else {
                workoutPage = workoutExecutionRepository.findByUserAndWorkoutName(
                        user, searchQuery, pageable
                );
            }
        }
        // caso 2 apenas data
        else if (startDate != null && endDate != null) {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, "executionDate"));
            workoutPage = workoutExecutionRepository.findByUserAndExecutionDateBetween(
                    user, startDate, endDate, pageable);
        }
        // nao tem nada
        else {
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, "executionDate"));
            workoutPage = workoutExecutionRepository.findByUser(user, pageable);        }


        List<WorkoutExecutionResponseDTO> workoutDTOs = workoutPage.getContent().stream()
                .map(WorkoutExecutionResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new AllWorkoutsResponseDTO(
                workoutDTOs,
                workoutPage.getTotalElements(),
                workoutPage.getNumber() + 1,
                workoutPage.getTotalPages(),
                workoutPage.getSize()
        );
    }


    public List<WorkoutExecution> findWorkoutExecutionsByUser(Integer userid) {
        return workoutExecutionRepository.findByUserId(userid);
    }

    public List<WorkoutExecution> findWorkoutExecutionsByUserAndStatus(Integer userid, WorkoutExecution.WorkoutStatus status) {
        return workoutExecutionRepository.findByUserIdAndStatus(userid, status);
    }

    public Optional<WorkoutExecution> findLatestWorkoutExecutionForUser(Integer userid) {
        return workoutExecutionRepository.findTopByUserIdOrderByStartTimeDesc(userid).stream().findFirst();
    }

    public List<WorkoutExecution> findWorkoutExecutionsByUserAndPeriod(Integer userid, LocalDateTime start, LocalDateTime end) {
        return workoutExecutionRepository.findByUserIdAndStartTimeBetween(userid, start, end);
    }

    // vai ser preciso start exercise e end?

    //registar sets
    @Transactional
    public SetExecution recordSetExecution(Integer exerciseExecutionId, SetExecution setExecutionDetails, Integer plannedSetId) {
        ExerciseExecution exerciseExecution = exerciseExecutionRepository.findById(exerciseExecutionId)
                .orElseThrow(() -> new RuntimeException("Exercise Execution not found: " + exerciseExecutionId));

        // create a new SetExecution for each recorded set
        SetExecution setExecution = new SetExecution();
        setExecution.setExerciseExecution(exerciseExecution);
        setExecution.setSetNumber(setExecutionDetails.getSetNumber()); // Use the set number from the DTO

        setExecution.setRepsPerformed(setExecutionDetails.getRepsPerformed());
        setExecution.setWeightPerformed(setExecutionDetails.getWeightPerformed());
        setExecution.setResTimePerformed(setExecutionDetails.getResTimePerformed());

        boolean completed = (setExecution.getRepsPerformed() != null && setExecution.getRepsPerformed() >= 0 &&
                setExecution.getWeightPerformed() != null && setExecution.getWeightPerformed() >= 0);
        setExecution.setCompleted(completed);

        // link to the planned set (SetData) if a plannedSetId is provided
        if (plannedSetId != null) {
            SetData plannedSet = setDataRepository.findById(plannedSetId)
                    .orElseThrow(() -> new RuntimeException("Planned Set (SetData) not found with id: " + plannedSetId));
            setExecution.setPlannedSet(plannedSet);
        }

        if (exerciseExecution.getPerformedSets() == null) {
            exerciseExecution.setPerformedSets(new ArrayList<>());
        }
        exerciseExecution.getPerformedSets().add(setExecution);

        return setExecutionRepository.save(setExecution);
    }
    /*
    @Transactional
    // provavelmente vai ser necessário.
    public SetExecution updateSetExecution(Integer workoutExecutionId, SetExecution setExecutionDetails) {

    }*/

    public Optional<ExerciseExecution> getExerciseExecutionById(Integer id) {
        return exerciseExecutionRepository.findById(id);
    }

    public void deleteWorkoutExecution(Integer executionId) {
        if (!workoutExecutionRepository.existsById(executionId)) {
            throw new RuntimeException("WorkoutExecution not found: " + executionId);
        }
        workoutExecutionRepository.deleteById(executionId);
    }

}
