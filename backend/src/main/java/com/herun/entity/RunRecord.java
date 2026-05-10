package com.herun.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "run_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RunRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double distanceKm;       // 달린 거리 (km)

    @Column(nullable = false)
    private Integer durationSeconds; // 달린 시간 (초)

    private Double paceMinPerKm;     // 페이스 (분/km)
    private Double caloriesBurned;   // 소모 칼로리

    @Column(columnDefinition = "TEXT")
    private String routeJson;        // GPS 경로 좌표 JSON

    private Double startLat;         // 시작 위도
    private Double startLng;         // 시작 경도

    @Column(updatable = false)
    private LocalDateTime runDate;

    @PrePersist
    protected void onCreate() { runDate = LocalDateTime.now(); }
}
