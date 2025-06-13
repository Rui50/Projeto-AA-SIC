package com.example.appfitness.repositories;

import com.example.appfitness.models.SetData;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SetExecutionRepository extends JpaRepository<SetExecution, Integer> {
    /*
    // obter os sets realizados de um exercicio realizado em ordem para nao ter de ordenar depois na frontend
    @Query("SELECT se FROM SetExecution se WHERE se.exerciseExecution.id = :exerciseExecutionId ORDER BY se.setNumber ASC")
    List<SetExecution> findByExerciseExecutionIdOrderBySetNumberAsc(Integer exerciseExecutionId);

    // obter um set específico, secalhar vai ser preciso para as dashboards se tivermos tempo de fazer uma coisa mais complicada
    @Query("SELECT se FROM SetExecution se WHERE se.exerciseExecution.id = :exerciseExecutionId AND se.setNumber = :setNumber")
    Optional<SetExecution> findByExerciseExecutionIdAndSetNumber(Integer exerciseExecutionId, Integer setNumber);

     */
    // Query to sum total volume for a user within a certain range
    @Query("SELECT SUM(s.weightPerformed * s.repsPerformed) FROM SetExecution s JOIN s.exerciseExecution ee JOIN ee.workoutExecution we WHERE we.user.id = :userId AND we.status = :status AND we.startTime BETWEEN :startDate AND :endDate")
    Double calculateTotalVolumeForUserAndDateRange(
            Integer userId,
            WorkoutExecution.WorkoutStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT ses FROM SetExecution ses WHERE ses.exerciseExecution.id IN :exerciseExecutionIds")
    List<SetExecution> findByExerciseExecutionIds(List<Integer> exerciseExecutionIds);

    long countByPlannedSet(SetData plannedSet);

    boolean existsByPlannedSet(SetData setData);
}