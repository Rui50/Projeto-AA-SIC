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
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner = :owner")
    List<WorkoutPlan> findByOwner(User owner);

    // Obtem os workouts ativos
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner = :owner AND wp.active = TRUE")
    Optional<WorkoutPlan> findByOwnerAndIsActiveTrue(User owner);

    // "filtrar por nome", isto provalmente nao é necessário pois fariamos no frontend
    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.owner = :owner AND wp.name = :name")
    Optional<WorkoutPlan> findByOwnerAndName(User owner, String name);
}
