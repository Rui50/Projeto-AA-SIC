package com.example.appfitness.repositories;

import com.example.appfitness.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    @Query("SELECT p FROM Professor p WHERE p.email = :email")
    Optional<Professor> findByEmail(String email);
}
