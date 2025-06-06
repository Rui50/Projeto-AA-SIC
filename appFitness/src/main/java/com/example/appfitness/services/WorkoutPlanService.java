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

/**
 * Serviço responsavel por gerenciar o Workoutplan, exerciseData e SetData
 */
@Service
public class WorkoutPlanService {
    private WorkoutPlanRepository workoutPlanRepository;
    private UserRepository userRepository;
    private ProfessorRepository professorRepository;
    private ExerciseRepository exerciseRepository;
    private ExerciseExecutionRepository exerciseExecutionRepository;
    private ExerciseDataRepository exerciseDataRepository;
    private WorkoutExecutionRepository workoutExecutionRepository;

    public WorkoutPlanService(WorkoutPlanRepository workoutPlanRepository,
                              ProfessorRepository professorRepository,
                              ExerciseRepository exerciseRepository,
                              UserRepository userRepository,
                              ExerciseDataRepository exerciseDataRepository,
                              ExerciseExecutionRepository exerciseExecutionRepository,
                              WorkoutExecutionRepository workoutExecutionRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.professorRepository = professorRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.exerciseDataRepository = exerciseDataRepository;
        this.workoutExecutionRepository = workoutExecutionRepository;
        this.exerciseExecutionRepository = exerciseExecutionRepository;
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

        return workoutPlanRepository.save(workoutPlan);
    }

    public Optional<WorkoutPlan> getWorkoutPlanById(Integer id) {
        return workoutPlanRepository.findById(id);
    }

    @Transactional
    public WorkoutPlan updateWorkoutPlan(Integer workoutPlanId, WorkoutPlanRequestDTO updateDTO) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new RuntimeException("Workout Plan not found: " + workoutPlanId));

        workoutPlan.setName(updateDTO.getName());
        workoutPlan.setDescription(updateDTO.getDescription());
        workoutPlan.setScheduleType(updateDTO.getScheduleType());
        workoutPlan.setScheduledDays(updateDTO.getScheduledDays());
        workoutPlan.setUpdatedAt(LocalDate.now());

        // secalhar algumas coisas fazemos o update de outra maneira tipo o active
        workoutPlan.setActive(updateDTO.isActive());

        // usar isto daqui para baixo depende
        // podemos ou fazer tudo em batch ou fazer calls a api em todos os ex
        Map<Integer, ExerciseData> existingExercises = new HashMap<>();
        if (workoutPlan.getExercises() != null) {
            for (ExerciseData ed : workoutPlan.getExercises()) {
                existingExercises.put(ed.getId(), ed);
            }
        }

        workoutPlan.getExercises().clear();

        if(updateDTO.getExercises() != null) {
            for(ExerciseDataRequestDTO exDTO :  updateDTO.getExercises()){
                ExerciseData exerciseData;

                // caso em que o exercicio existe
                if(exDTO.getId() != null) {
                    exerciseData = existingExercises.get(exDTO.getId());
                    if (exerciseData == null) {

                        throw new RuntimeException("ExerciseData with ID " + exDTO.getExerciseId() +
                                " not found or not linked to Workout Plan " + workoutPlanId + " for update.");
                    }
                }
                else {
                    exerciseData = new ExerciseData();
                }

                Exercise exercise = exerciseRepository.findById(exDTO.getExerciseId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found: " + exDTO.getExerciseId()));
                exerciseData.setExercise(exercise);
                exerciseData.setNote(exDTO.getNote());

                // agora para os sets mesma logica
                Map<Integer, SetData> existingSets = new HashMap<>();
                if (exerciseData.getPlannedSets() != null) {
                    for (SetData sd : exerciseData.getPlannedSets()) {
                        existingSets.put(sd.getId(), sd);
                    }
                }
                exerciseData.getPlannedSets().clear();

                if (exDTO.getPlannedSets() != null) {
                    for (SetDataDTO setDTO : exDTO.getPlannedSets()) {
                        SetData setData;

                        // caso de haver o set apenas atualizamos
                        if (setDTO.getId() != null) {
                            setData = existingSets.get(setDTO.getId());
                            if (setData == null) {
                                throw new RuntimeException("SetData with ID " + setDTO.getId() +
                                        " not found or not linked to ExerciseData " + exerciseData.getId() + " for update.");
                            }
                        } else {
                            // caso contrario inserimos
                            setData = new SetData();
                        }

                        // update set data
                        setData.setSetNumber(setDTO.getSetNumber());
                        setData.setRepsPlanned(setDTO.getReps());
                        setData.setWeightPlanned(setDTO.getWeight());
                        setData.setRestTimeSugested(setDTO.getRestTimeSugested());


                        exerciseData.addPlannedSet(setData);
                    }
                }

                workoutPlan.addExercise(exerciseData);

            }
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
            // Aqui assume-se que `getScheduledDays()` devolve uma List<String> tipo ["MONDAY", "WEDNESDAY"]
            if (plan.getScheduledDays() != null) {
                for (DayOfWeek day : plan.getScheduledDays()) {
                    String dayStr = capitalize(day.toString().toLowerCase());
                    schedule.computeIfAbsent(dayStr, k -> new ArrayList<>()).add(plan.getId());
                }
            }
        }
        return schedule;
    }

    // Helper para capitalizar só a primeira letra
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}


