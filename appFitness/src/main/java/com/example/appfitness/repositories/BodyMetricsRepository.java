package com.example.appfitness.repositories;

import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, Integer> {
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user = :user")
    List<BodyMetrics> findByUser(User user);

    // Para obter a mais recente, ordenamos e pegamos o primeiro resultado.
    @Query("SELECT bm FROM BodyMetrics bm WHERE bm.user = :user ORDER BY bm.updatedAt DESC")
    List<BodyMetrics> findTopByUserOrderByMeasurementDateDesc(User user);
    // Retorna lista, mas o serviço pega no primeiro
}
