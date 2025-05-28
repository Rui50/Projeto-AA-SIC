package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jmx.support.MetricType;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double height;
    private Double weight;
    private Double bmi;
    private Double bodyFatPercentage;

    @Column(nullable = false)
    private LocalDate updatedAt;

    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum MetricType {
        METRIC,
        IMPERIAL
    }
}
