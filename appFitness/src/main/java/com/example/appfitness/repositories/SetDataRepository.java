package com.example.appfitness.repositories;

import com.example.appfitness.models.ExerciseData;
import com.example.appfitness.models.SetData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetDataRepository extends JpaRepository<SetData, Integer> {
    // devolver ja ordenado, evito ter de ordenar na frontend
    @Query("SELECT sd FROM SetData sd WHERE sd.exerciseData = :exerciseData ORDER BY sd.setNumber ASC")
    List<SetData> findByExerciseDataOrderBySetNumberAsc(ExerciseData exerciseData);

}
