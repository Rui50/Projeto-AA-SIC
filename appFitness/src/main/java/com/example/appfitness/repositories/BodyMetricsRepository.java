package com.example.appfitness.repositories;

import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Integer> {
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId")
    List<BodyMetrics> findByUserId(Integer userId);

    // Para obter a mais recente, ordenamos e pegamos o primeiro resultado.
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId ORDER BY bm.updatedAt DESC")
    List<BodyMetrics> findTopByUserIdOrderByMeasurementDateDesc(Integer userId);
    // Retorna lista, mas o serviço pega no primeiro

    // Para obter a partir de uma certa data
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user.id = :userId AND bm.updatedAt >= :startDate ORDER BY bm.updatedAt ASC")
    List<BodyMetrics> findByUserIdAndUpdatedAt(Integer userId, LocalDate startDate);
}
