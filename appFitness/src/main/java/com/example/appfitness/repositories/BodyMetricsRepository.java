package com.example.appfitness.repositories;

import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Integer> {
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId")
    List<BodyMetrics> findByUserId(Integer userId);

    // Para obter a mais recente, ordenamos e pegamos o primeiro resultado.
    @Query(value = "SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId AND bm.updatedAt <= :targetDate ORDER BY bm.updatedAt DESC LIMIT 1")
    Optional<BodyMetrics> findClosestBeforeDateForUser(Integer userId, LocalDateTime targetDate);
    // Retorna lista, mas o serviço pega no primeiro

    Optional<BodyMetrics> findFirstByUser_IdOrderByUpdatedAtDesc(Integer userId);

    Optional<BodyMetrics> findTop1ByUserIdOrderByUpdatedAtDescIdDesc(Integer userId);


    // Para obter a partir de uma certa data
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId AND bm.updatedAt >= :startDate ORDER BY bm.updatedAt ASC")
    List<BodyMetrics> findByUserIdAndUpdatedAt(Integer userId, LocalDate startDate);

    List<BodyMetrics> findByUser_IdAndUpdatedAtBetweenOrderByUpdatedAtAsc(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
}
