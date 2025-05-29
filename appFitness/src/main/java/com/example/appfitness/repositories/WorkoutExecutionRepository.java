package com.example.appfitness.repositories;

import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutExecutionRepository extends JpaRepository<WorkoutExecution, Integer> {
    // buscar as workout executions de um user
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId")
    List<WorkoutExecution> findByUserId(Integer userId);

    // se quisermos os que ele deu skip or completou apenas
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId AND we.status = :status")
    List<WorkoutExecution> findByUserIdAndStatus(Integer userId, WorkoutExecution.WorkoutStatus status);

    // para conseguirmos pegar no ultimo que ele conclui, ou tipo top 3
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId ORDER BY we.startTime DESC")
    List<WorkoutExecution> findTopByUserIdOrderByStartTimeDesc(Integer userId); // Retorna lista, mas o serviço obtem o 1 apenas

    // Para a dashboard de melhorias usamos este
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user.id = :userId AND we.startTime BETWEEN :start AND :end")
    List<WorkoutExecution> findByUserIdAndStartTimeBetween(Integer userId, LocalDateTime start, LocalDateTime end);

    // adicionado automatico pelo intelij, ver se funciona
    Optional<WorkoutExecution> findByUserAndStatus(User user, WorkoutExecution.WorkoutStatus workoutStatus);
}
