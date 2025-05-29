package com.example.appfitness.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.appfitness.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    Optional<String> findEmailById(String id);

    @Query(value="SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM users WHERE email = ?1", nativeQuery = true)
    Integer checkEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id in :usersIds")
    List<User> findUsersByIds(List<Integer> usersIds);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:term% OR u.email LIKE %:term%")
    Optional<List<User>> findByTerm(String term);
}
