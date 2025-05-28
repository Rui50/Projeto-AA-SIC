package com.example.appfitness.repositories;

import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutExecutionRepository extends JpaRepository<WorkoutExecution, Integer> {
    // buscar as workout executions de um user
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user = :user")
    List<WorkoutExecution> findByUser(User user);

    // se quisermos os que ele deu skip or completou apenas
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user = :user AND we.status = :status")
    List<WorkoutExecution> findByUserAndStatus(User user, WorkoutExecution.WorkoutStatus status);

    // para conseguirmos pegar no ultimo que ele conclui, ou tipo top 3
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user = :user ORDER BY we.startTime DESC")
    List<WorkoutExecution> findTopByUserOrderByStartTimeDesc(User user); // Retorna lista, mas o serviço obtem o 1 apenas

    // Para a dashboard de melhorias usamos este
    @Query("SELECT we FROM WorkoutExecution we WHERE we.user = :user AND we.startTime BETWEEN :start AND :end")
    List<WorkoutExecution> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
}
