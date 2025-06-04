package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.jmx.support.MetricType;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BodyMetrics")
@ToString
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
