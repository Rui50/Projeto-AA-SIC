package com.example.appfitness.repositories;

import com.example.appfitness.models.User;
import com.example.appfitness.models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Integer> {
    // obter os workout plans de um user
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.isDeleted = FALSE")
    List<WorkoutPlan> findByOwnerId(Integer userId);

    // Obtem os workouts ativos
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.active = TRUE")
    Optional<WorkoutPlan> findByOwnerIdAndIsActiveTrue(Integer userId);

    // "filtrar por nome", isto provalmente nao é necessário pois fariamos no frontend
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner.id = :userId AND wp.name = :planName")
    Optional<WorkoutPlan> findByOwnerIdAndName(Integer userId, String planName);

    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.createdBy = :professorId AND wp.owner.id = :ownerId AND wp.isDeleted = FALSE ORDER BY wp.updatedAt DESC")
    List<WorkoutPlan> findByCreatedByAndOwnerIdOrderByUpdatedAtDesc(
            Integer professorId,
            Integer ownerId
    );
}
