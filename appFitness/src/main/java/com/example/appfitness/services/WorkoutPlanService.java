package com.example.appfitness.services;

import com.example.appfitness.DTOs.WorkoutPlan.ExerciseDataRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.SetDataDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanRequestDTO;
import com.example.appfitness.DTOs.WorkoutPlan.WorkoutPlanResponseDTO;
import com.example.appfitness.models.*;
import com.example.appfitness.repositories.*;
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

        if (ownerId != creatorId){
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
    public WorkoutPlan updateWorkoutPlan(Integer workoutPlanId, WorkoutPlanRequestDTO updateDTO) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        System.out.println("REQUEST RECEIVED FOR UPDATE " + updateDTO);

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

        // mapa id -> exercise data            M
        Map<Integer, ExerciseData> existingExercisesMap = workoutPlan.getExercises().stream()
                .filter(ed -> ed.getId() != null) // nao ter os novos
                .collect(Collectors.toMap(ExerciseData::getId, exerciseData -> exerciseData));

        Set<Integer> exercisesToKeepIds = new HashSet<>();
        
        // guarda os exercicios que vao continuar no plano ou levar softdelete
        List<ExerciseData> exercisesToKeep = new ArrayList<>();
        
        // exercicios que vao levar delete
        List<ExerciseData> exercisesToDelete = new ArrayList<>(); 

        if (updateDTO.getExercises() != null) {
            for (ExerciseDataRequestDTO exDTO : updateDTO.getExercises()) {
                ExerciseData exerciseData;

                // se exDTO tem id e existe no workoutplan
                if (exDTO.getId() != null && existingExercisesMap.containsKey(exDTO.getId())) {
                    exerciseData = existingExercisesMap.get(exDTO.getId());
                    exercisesToKeepIds.add(exerciseData.getId());
                }
                // se é um novo exerc, cria inst
                else {
                    exerciseData = new ExerciseData();
                    exerciseData.setWorkoutPlan(workoutPlan); 
                }

                // exercicio referenciado
                Exercise exercise = exerciseRepository.findById(exDTO.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found: " + exDTO.getExerciseId()));

                exerciseData.setExercise(exercise);
                exerciseData.setNote(exDTO.getNote());
                exerciseData.setDeleted(false);

                // map id -> setData 
                Map<Integer, SetData> existingSetsMap = exerciseData.getPlannedSets().stream()
                        .filter(sd -> sd.getId() != null)
                        .collect(Collectors.toMap(SetData::getId, setData -> setData));
                
                
                // ids dos sets que vao ficar
                Set<Integer> setsToKeepIds = new HashSet<>();
                // This list will hold all sets that should remain attached to this exercise,
                // including new, updated, and soft-deleted ones.
                
                // lista com sets novos, que vao levar update ou soft delete
                List<SetData> setsToKeepForExerciseData = new ArrayList<>();
                
                // lista para track de sets para delete
                List<SetData> setsToDelete = new ArrayList<>(); 

                if (exDTO.getPlannedSets() != null) {
                    for (SetDataDTO setDTO : exDTO.getPlannedSets()) {
                        SetData setData;

                        if (setDTO.getId() != null && existingSetsMap.containsKey(setDTO.getId())) {
                            setData = existingSetsMap.get(setDTO.getId());
                            setsToKeepIds.add(setData.getId());
                        }
                        // se é novo cia inst
                        else {
                            setData = new SetData();
                            setData.setExerciseData(exerciseData);
                        }

                        setData.setSetNumber(setDTO.getSetNumber());
                        setData.setRepsPlanned(setDTO.getReps());
                        setData.setWeightPlanned(setDTO.getWeight());
                        setData.setRestTimeSugested(setDTO.getRestTimeSugested());
                        setData.setDeleted(false);

                        setsToKeepForExerciseData.add(setData);
                    }
                }

                // obter quais setData vao ser apagados ou levar soft del
                List<SetData> setsRemovedFromDTO = existingSetsMap.values().stream()
                        .filter(setData -> !setsToKeepIds.contains(setData.getId()))
                        .toList();

                for (SetData setData : setsRemovedFromDTO) {
                    if (setExecutionRepository.countByPlannedSet(setData) > 0) {
                        setData.setDeleted(true);
                        setsToKeepForExerciseData.add(setData);
                    } else {
                        setsToDelete.add(setData);
                    }
                }

                if (!setsToDelete.isEmpty()) {
                    setDataRepository.deleteAll(setsToDelete);
                }

                exerciseData.getPlannedSets().clear();
                exerciseData.getPlannedSets().addAll(setsToKeepForExerciseData);

                exercisesToKeep.add(exerciseData);
            }
        }

        // saber quais ExerciseDAta vao levar delete ou softDelete
        List<ExerciseData> exercisesRemovedFromDTO = existingExercisesMap.values().stream()
                .filter(exerciseData -> !exercisesToKeepIds.contains(exerciseData.getId()))
                .toList();

        for (ExerciseData exerciseData : exercisesRemovedFromDTO) {
            Long hasExecutions = exerciseExecutionRepository.countByExerciseData(exerciseData);

            if (hasExecutions > 0) {
                exerciseData.setDeleted(true);

                // so para ter a certeza, se um ex leva soft-delete, os sets também
                List<SetData> setsToDeleteUnderSoftDeletedExercise = new ArrayList<>();
                exerciseData.getPlannedSets().forEach(setData -> {
                    if (setExecutionRepository.countByPlannedSet(setData) > 0) {
                        setData.setDeleted(true);
                    } else {
                        setsToDeleteUnderSoftDeletedExercise.add(setData);
                    }
                });
                if (!setsToDeleteUnderSoftDeletedExercise.isEmpty()) {
                    setDataRepository.deleteAll(setsToDeleteUnderSoftDeletedExercise);
                }
                exercisesToKeep.add(exerciseData);
            } else {

                List<SetData> setsToDeleteUnderPhysicalDeletedExercise = new ArrayList<>();
                exerciseData.getPlannedSets().forEach(setData -> {
                    if (setExecutionRepository.countByPlannedSet(setData) == 0) {
                        setsToDeleteUnderPhysicalDeletedExercise.add(setData);
                    } else {
                        setData.setDeleted(true);
                        exerciseData.addPlannedSet(setData);
                        exercisesToKeep.add(exerciseData);
                    }
                });

                if (!setsToDeleteUnderPhysicalDeletedExercise.isEmpty()) {
                    setDataRepository.deleteAll(setsToDeleteUnderPhysicalDeletedExercise);
                }

                //so apagar se nao tiver softdel
                if (hasExecutions == 0 && !exerciseData.isDeleted()) { //
                    exercisesToDelete.add(exerciseData);
                }
            }
        }

        if (!exercisesToDelete.isEmpty()) {
            exerciseDataRepository.deleteAll(exercisesToDelete);
        }

        workoutPlan.getExercises().clear();
        workoutPlan.getExercises().addAll(exercisesToKeep);

        if (workoutPlan.getOwner().getId() != workoutPlan.getCreatedBy()){
            User user = userRepository.findById(workoutPlan.getCreatedBy()).get();
            String message = String.format("The workout plan '%s' was updated by your professor '%s'.", workoutPlan.getName() ,user.getName());

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



    // um metodo so de update ou metemos metodo para adicionar exericicio/remover etc

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
        workoutPlanRepository.save(workoutPlan); // cascade saves the rest?

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


