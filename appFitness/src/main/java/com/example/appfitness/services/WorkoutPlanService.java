package com.example.appfitness.services;

import com.example.appfitness.DTOs.WorkoutPlan.ExerciseDataRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.SetDataDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanResponseDTO;
import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

/**
 * Serviço responsavel por gerenciar o Workoutplan, exerciseData e SetData
 */
@Service
public class WorkoutPlanService {
    private WorkoutPlanRepository workoutPlanRepository;
    private UserRepository userRepository;
    private ExerciseRepository exerciseRepository;
    private ExerciseExecutionRepository exerciseExecutionRepository;
    private ExerciseDataRepository exerciseDataRepository;
    private WorkoutExecutionRepository workoutExecutionRepository;
    private SetExecutionRepository setExecutionRepository;
    private SetDataRepository setDataRepository;
    private NotificationService notificationService;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ExerciseRepository exerciseRepository,
                              UserRepository userRepository,
                              ExerciseDataRepository exerciseDataRepository,
                              ExerciseExecutionRepository exerciseExecutionRepository,
                              WorkoutExecutionRepository workoutExecutionRepository,
                              SetExecutionRepository setExecutionRepository,
                              SetDataRepository setDataRepository,
                              NotificationService notificationService) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.exerciseDataRepository = exerciseDataRepository;
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.exerciseExecutionRepository = exerciseExecutionRepository;
        this.setExecutionRepository = setExecutionRepository;
        this.setDataRepository = setDataRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public List<WorkoutPlan> getWorkoutPlansByOwnerId(Integer ownerId) {
        return workoutPlanRepository.findByOwnerId(ownerId);
    }

    @Transactional
    public WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan, Integer ownerId, Integer creatorId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner user not found " + ownerId));
        workoutPlan.setOwner(owner);
        workoutPlan.setCreatedBy(creatorId);

        if (workoutPlan.getScheduleType() == null) {
            workoutPlan.setScheduleType(WorkoutPlan.WorkoutScheduleType.Free);
        }

        // doesnt start as active
        workoutPlan.setActive(false);
        workoutPlan.setDeleted(false);
        workoutPlan.setExercises(new ArrayList<>());
        workoutPlan.setUpdatedAt(LocalDate.now());

        if (!ownerId.equals(creatorId)){
            User user = userRepository.findById(creatorId).get();
            String message = String.format("The workout plan '%s' has been assigned to you by your professor '%s'.", workoutPlan.getName() ,user.getName());

            notificationService.createNotification(
                    ownerId,
                    message,
                    Notification.NotificationType.WORKOUT_CREATED
            );
        }

        return workoutPlanRepository.save(workoutPlan);
    }

    public Optional<WorkoutPlan> getWorkoutPlanById(Integer id) {
        return workoutPlanRepository.findById(id);
    }
    @Transactional
    public WorkoutPlan updateWorkoutPlan(Integer workoutPlanId, WorkoutPlanRequestDTO updateDTO, Integer userId) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        System.out.println("REQUEST RECEIVED FOR UPDATE " + updateDTO);

        // update info básica
        workoutPlan.setName(updateDTO.getName());
        workoutPlan.setDescription(updateDTO.getDescription());
        workoutPlan.setScheduleType(updateDTO.getScheduleType());
        if (updateDTO.getScheduledDays() != null) {
            workoutPlan.setScheduledDays(new HashSet<>(updateDTO.getScheduledDays()));
        } else {
            workoutPlan.setScheduledDays(new HashSet<>());
        }
        workoutPlan.setUpdatedAt(LocalDate.now());
        workoutPlan.setActive(updateDTO.isActive());

        // mapa id -> exercicio
        Map<Integer, ExerciseData> existingExercisesMap = workoutPlan.getExercises().stream()
                .filter(ed -> ed.getId() != null)
                .collect(Collectors.toMap(ExerciseData::getId, exerciseData -> exerciseData));

        // lista para guardar exercicios a levar update ou novos
        List<ExerciseData> exercisesToUpdateOrAdd = new ArrayList<>();

        if (updateDTO.getExercises() != null) {
            for (ExerciseDataRequestDTO exDTO : updateDTO.getExercises()) {
                ExerciseData exerciseData;

                // se é existente
                if (exDTO.getId() != null && existingExercisesMap.containsKey(exDTO.getId())) {
                    exerciseData = existingExercisesMap.remove(exDTO.getId());
                }
                // se é novo exercicio
                else {
                    exerciseData = new ExerciseData();
                    exerciseData.setWorkoutPlan(workoutPlan); // set parent
                }

                // update campos do exerciseData comuns
                Exercise exercise = exerciseRepository.findById(exDTO.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found: " + exDTO.getExerciseId()));
                exerciseData.setExercise(exercise);
                exerciseData.setNote(exDTO.getNote());
                exerciseData.setDeleted(false);

                // mapa id -> setData
                Map<Integer, SetData> existingSetsMap = exerciseData.getPlannedSets().stream()
                        .filter(sd -> sd.getId() != null)
                        .collect(Collectors.toMap(SetData::getId, setData -> setData));


                List<SetData> setsToUpdateOrAdd = new ArrayList<>();

                if (exDTO.getPlannedSets() != null) {
                    for (SetDataDTO setDTO : exDTO.getPlannedSets()) {
                        SetData setData;

                        // se existe
                        if (setDTO.getId() != null && existingSetsMap.containsKey(setDTO.getId())) {
                            setData = existingSetsMap.remove(setDTO.getId());
                        }
                        // se é um set novo
                        else {
                            setData = new SetData();
                            setData.setExerciseData(exerciseData); // set parent
                        }
                        setData.setSetNumber(setDTO.getSetNumber());
                        setData.setRepsPlanned(setDTO.getReps());
                        setData.setWeightPlanned(setDTO.getWeight());
                        setData.setRestTimeSugested(setDTO.getRestTimeSugested());
                        setData.setDeleted(false);

                        setsToUpdateOrAdd.add(setData);
                    }
                }

                // para lidar com os sets a ser removidos (nao tao no dto)
                for (SetData setData : existingSetsMap.values()) {
                    if (setExecutionRepository.countByPlannedSet(setData) > 0) {
                        setData.setDeleted(true); // se tiverem execuções damos soft-del
                        setsToUpdateOrAdd.add(setData);
                    }
                    // else sao deleted por causa do orphanRemoval = true
                }

                exerciseData.getPlannedSets().clear();
                exerciseData.getPlannedSets().addAll(setsToUpdateOrAdd);

                exercisesToUpdateOrAdd.add(exerciseData);
            }
        }

        // lidar com exercicios removidos (nao presentes no dto)
        for (ExerciseData exerciseData : existingExercisesMap.values()) {
            Long hasExecutions = exerciseExecutionRepository.countByExerciseData(exerciseData);

            if (hasExecutions > 0) {
                exerciseData.setDeleted(true); // se tem execuções leva soft delete

                // se o ex leva soft delete temos ainda de lidar com os seus sets,
                List<SetData> setsFromSoftDeletedExercise = new ArrayList<>();
                for (SetData setData : exerciseData.getPlannedSets()) {
                    if (setExecutionRepository.countByPlannedSet(setData) > 0) {
                        setData.setDeleted(true); // soft delete se tem execuções
                        setsFromSoftDeletedExercise.add(setData);
                    }
                }
                exerciseData.getPlannedSets().clear();
                exerciseData.getPlannedSets().addAll(setsFromSoftDeletedExercise);

                exercisesToUpdateOrAdd.add(exerciseData);
            }
        }

        workoutPlan.getExercises().clear();
        workoutPlan.getExercises().addAll(exercisesToUpdateOrAdd);

        if (workoutPlan.getOwner().getId() != workoutPlan.getCreatedBy() && workoutPlan.getOwner().getId() != userId) {
            User user = userRepository.findById(workoutPlan.getCreatedBy())
                    .orElseThrow(() -> new EntityNotFoundException("Creator user not found for notification."));
            String message = String.format("The workout plan '%s' was updated by your professor '%s'.", workoutPlan.getName(), user.getName());

            notificationService.createNotification(
                    workoutPlan.getOwner().getId(),
                    message,
                    Notification.NotificationType.WORKOUT_UPDATE
            );
        }

        return workoutPlanRepository.save(workoutPlan);
    }


    @Transactional
    public void deleteWorkoutPlanById(Integer id) {
        if (!workoutPlanRepository.existsById(id)) {
            throw new RuntimeException("Workout Plan not found: " + id);
        }

        WorkoutPlan workoutPlan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + id));

        boolean hasExecutions = workoutExecutionRepository.existsByWorkoutPlanId(id);
        if (hasExecutions) {
            workoutPlan.setDeleted(true);
            workoutPlanRepository.save(workoutPlan);
        }
        else {
            workoutPlanRepository.deleteById(id);
        }

    }

    @Transactional
    public WorkoutPlan updateWorkoutPlanActiveStatus(Integer workoutPlanId, boolean activeStatus) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        workoutPlan.setActive(activeStatus);
        return workoutPlanRepository.save(workoutPlan);
    }

    @Transactional
    public ExerciseData addExerciseToWorkoutPlan(Integer workoutPlanId, Integer exerciseId, List<SetData> plannedSets, String note) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found: " + exerciseId));

        ExerciseData exerciseData = new ExerciseData();
        exerciseData.setExercise(exercise);
        exerciseData.setWorkoutPlan(workoutPlan);
        exerciseData.setNote(note);
        exerciseData.setDeleted(false);

        for(SetData setData : plannedSets) {
            exerciseData.addPlannedSet(setData);
        }

        workoutPlan.getExercises().add(exerciseData);
        workoutPlanRepository.save(workoutPlan);

        return exerciseDataRepository.save(exerciseData);
    }

    @Transactional
    public ExerciseData updateExerciseInWorkoutPlan(Integer workoutPlanId, Integer exerciseDataId, List<SetData> updatedPlannedSets, String updatedNote) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found " + workoutPlanId));

        ExerciseData exerciseData = exerciseDataRepository.findById(exerciseDataId)
                .orElseThrow(() -> new RuntimeException("Exercise Data not: " + exerciseDataId));

        // testar erro na frontend
        if (exerciseData.getWorkoutPlan().getId() != workoutPlanId) {
            throw new RuntimeException("Exercise Data " + exerciseDataId + " not from Workout Plan " + workoutPlanId);
        }

        exerciseData.setNote(updatedNote);
        // for now just fully replace
        exerciseData.getPlannedSets().clear();
        for (SetData setData : updatedPlannedSets) {
            exerciseData.addPlannedSet(setData);
        }
        return exerciseDataRepository.save(exerciseData);
    }

    @Transactional
    public void removeExerciseFromWorkoutPlan(Integer workoutPlanId, Integer exerciseDataId) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        ExerciseData exerciseDataToRemove = exerciseDataRepository.findById(exerciseDataId)
                .orElseThrow(() -> new RuntimeException("Exercise Data not found: " + exerciseDataId));

        if (exerciseDataToRemove.getWorkoutPlan().getId() != workoutPlanId) {
            throw new RuntimeException("Exercise Data " + exerciseDataId + " not from Workout Plan " + workoutPlanId);
        }

        boolean hasExecutions = exerciseExecutionRepository.existsByExerciseDataId(exerciseDataId);

        if(hasExecutions) {
            exerciseDataToRemove.setDeleted(true);
            exerciseDataRepository.save(exerciseDataToRemove);
        }
        else {
            workoutPlan.getExercises().remove(exerciseDataToRemove);
            workoutPlanRepository.save(workoutPlan);

            exerciseDataRepository.delete(exerciseDataToRemove);
        }
    }

    public WorkoutPlanResponseDTO toResponseDTOfix(WorkoutPlan workoutPlan) {
        String createdByUserName = "";
        if (workoutPlan.getCreatedBy() != null) {
            Optional<User> creatorUser = userRepository.findById(workoutPlan.getCreatedBy());
            if (creatorUser.isPresent()) {
                createdByUserName = creatorUser.get().getName();
            }
        }

        // filtrar os que foram "soft-deleted"

        List<ExerciseData> notDeleted = workoutPlan.getExercises().stream()
                .filter(ed -> !ed.isDeleted())
                .toList();

        workoutPlan.setExercises(notDeleted);
        return WorkoutPlanResponseDTO.fromEntity(workoutPlan, createdByUserName);
    }

    @Transactional
    public Map<String, List<Integer>> getActiveWorkoutPlansByDayForOwner(Integer ownerId) { 
        List<WorkoutPlan> activePlans = workoutPlanRepository.findAllActiveByOwnerId(ownerId);

        Map<String, List<Integer>> schedule = new HashMap<>();
        for (WorkoutPlan plan : activePlans) {
            if (plan.getScheduleType() == WorkoutPlan.WorkoutScheduleType.Fixed) {
                // Aqui assume-se que `getScheduledDays()` devolve uma List<String> tipo ["MONDAY", "WEDNESDAY"]
                if (plan.getScheduledDays() != null) {
                    for (DayOfWeek day : plan.getScheduledDays()) {
                        String dayStr = capitalize(day.toString().toLowerCase());
                        schedule.computeIfAbsent(dayStr, k -> new ArrayList<>()).add(plan.getId());
                    }
                }
            }
        }
        return schedule; // Retorna um mapa onde a chave é o dia da semana e o valor é uma lista de IDs de planos de treino ativos
    }

    // Helper para capitalizar só a primeira letra
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}


