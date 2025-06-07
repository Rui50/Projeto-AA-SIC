package com.example.appfitness.repositories;

import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutExecution;
import com.example.appfitness.models.SetExecution;
import com.example.appfitness.models.WorkoutPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutExecutionRepository extends JpaRepository<WorkoutExecution, Integer> {
    // buscar as workout executions de um user
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId")
    List<WorkoutExecution> findByUserId(Integer userId);

    // ver se existe execuções de um workout
    boolean existsByWorkoutPlanId(Integer workoutPlanId);

    // se quisermos os que ele deu skip or completou apenas
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId AND we.status = :status")
    List<WorkoutExecution> findByUserIdAndStatus(Integer userId, WorkoutExecution.WorkoutStatus status);

    // para conseguirmos pegar no ultimo que ele conclui, ou tipo top 3
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId ORDER BY we.startTime DESC")
    List<WorkoutExecution> findTopByUserIdOrderByStartTimeDesc(Integer userId);

    // Para a dashboard de melhorias usamos este
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId AND we.startTime BETWEEN :start AND :end")
    List<WorkoutExecution> findByUserIdAndStartTimeBetween(Integer userId, LocalDateTime start, LocalDateTime end);

    // adicionado automatico pelo intelij, ver se funciona
    Optional<WorkoutExecution> findByUserAndStatus(User user, WorkoutExecution.WorkoutStatus workoutStatus);

    @Query("SELECT COUNT(we) FROM WorkoutExecution we WHERE we.user.id = :userId AND we.status = :status AND we.startTime BETWEEN :start AND :end")
    Long countByUserIdAndStatusAndStartTimeBetween(Integer userId, WorkoutExecution.WorkoutStatus status, LocalDateTime start, LocalDateTime end);

    Optional<WorkoutExecution> findTopByUserIdAndStatusOrderByStartTimeDesc(int id, WorkoutExecution.WorkoutStatus workoutStatus);

    // for the all workouts with all filters
    @Query("SELECT we FROM WorkoutExecution we " +
            "WHERE we.user = :user " +
            "AND (:searchTerm IS NULL OR LOWER(we.workoutPlan.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND we.executionDate BETWEEN :startDate AND :endDate")
    Page<WorkoutExecution> findWorkoutsByUserAndFilters(
            User user,
            String searchTerm,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    // for start and end date
    Page<WorkoutExecution> findByUserAndExecutionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    // only searchquery
    @Query("SELECT we FROM WorkoutExecution we " +
            "WHERE we.user = :user " +
            "AND LOWER(we.workoutPlan.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    Page<WorkoutExecution> findByUserAndWorkoutName(
            User user,
            String searchQuery,
            Pageable pageable);

    Page<WorkoutExecution> findByUser(User user, Pageable pageable);

    @Query("SELECT we FROM WorkoutExecution we " +
            "JOIN FETCH we.workoutPlan wp " +
            "WHERE we.user.id = :userId " +
            "AND we.status = :status " +
            "AND wp.isDeleted is False " +
            "AND we.executionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY we.executionDate ASC, wp.id ASC")
    List<WorkoutExecution> findCompletedWorkoutExecutionsWithWorkoutPlan(
            Integer userId,
            WorkoutExecution.WorkoutStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    // Check if there is an in-progress workout execution for a user
    @Query("SELECT COUNT(we) > 0 FROM WorkoutExecution we WHERE we.user.id = :userId AND we.status = 'IN_PROGRESS'")
    boolean existsInProgressByUserId(Integer userId);

    @Query("SELECT COUNT(we) > 0 FROM WorkoutExecution we " +
            "WHERE we.user = :user " +
            "AND we.workoutPlan = :workoutPlan " +
            "AND we.executionDate = :executionDate " +
            "AND we.status = :status")
    boolean existsByUserAndWorkoutPlanAndExecutionDateAndStatus(
            User user,
            WorkoutPlan workoutPlan,
            LocalDate executionDate,
            WorkoutExecution.WorkoutStatus status
    );
}
