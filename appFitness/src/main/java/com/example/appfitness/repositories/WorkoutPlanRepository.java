package com.example.appfitness.repositories;

import com.example.appfitness.models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
    // Obter os workout plans de um user
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.isDeleted = FALSE")
    List<WorkoutPlan> findByOwnerId(Integer userId);

    // Obtem os workouts ativos para um user (retorna só um, cuidado)
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.active = TRUE")
    Optional<WorkoutPlan> findByOwnerIdAndIsActiveTrue(Integer userId);

    // "Filtrar por nome"
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.name = :planName")
    Optional<WorkoutPlan> findByOwnerIdAndName(Integer userId, String planName);

    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.createdBy = :professorId AND wp.owner.id = :ownerId AND wp.isDeleted = FALSE ORDER BY wp.updatedAt DESC")
    List<WorkoutPlan> findByCreatedByAndOwnerIdOrderByUpdatedAtDesc(
            Integer professorId,
            Integer ownerId
    );

    List<WorkoutPlan> findByOwnerIdAndScheduleType(Integer ownerId, WorkoutPlan.WorkoutScheduleType scheduleType);

    // NOVOS - para o calendário semanal
    // Busca todos os ativos de todos os usuários (para mostrar no calendário geral)
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.active = TRUE AND wp.isDeleted = FALSE")
    List<WorkoutPlan> findAllActive();

    // Busca todos os ativos de UM usuário específico (se quiseres exibir só para um user)
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :ownerId AND wp.active = TRUE AND wp.isDeleted = FALSE")
    List<WorkoutPlan> findAllActiveByOwnerId(Integer ownerId);
}
