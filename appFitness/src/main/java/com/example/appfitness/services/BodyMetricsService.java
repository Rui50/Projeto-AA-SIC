package com.example.appfitness.services;

import com.example.appfitness.models.BodyMetrics;
import com.example.appfitness.models.User;
import com.example.appfitness.repositories.BodyMetricsRepository;
import com.example.appfitness.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BodyMetricsService {
    private BodyMetricsRepository bodyMetricsRepository;

    public BodyMetricsService(BodyMetricsRepository bodyMetricsRepository) {
        this.bodyMetricsRepository = bodyMetricsRepository;
    }

    public BodyMetrics createBodyMetrics(BodyMetrics bodyMetrics) {
        return bodyMetricsRepository.save(bodyMetrics);
    }

    public List<BodyMetrics> getBodyMetricsFromUser(Integer id) {
        return bodyMetricsRepository.findByUserId(id);
    }

    public Optional<BodyMetrics> latestBodyMetricUser(Integer id) {
        return bodyMetricsRepository.findTop1ByUserIdOrderByUpdatedAtDescIdDesc(id);
    }

    public List<BodyMetrics> findBodyMetricsByUserAndDate(Integer id, LocalDate date) {
        return bodyMetricsRepository.findByUserIdAndUpdatedAt(id, date);
    }
}
