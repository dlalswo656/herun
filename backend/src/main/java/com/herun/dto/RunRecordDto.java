package com.herun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class RunRecordDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SaveRequest {
        private Double distanceKm;
        private Integer durationSeconds;
        private Double paceMinPerKm;
        private Double caloriesBurned;
        private String routeJson;
        private Double startLat;
        private Double startLng;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private Double distanceKm;
        private Integer durationSeconds;
        private Double paceMinPerKm;
        private Double caloriesBurned;
        private String routeJson;
        private Double startLat;
        private Double startLng;
        private LocalDateTime runDate;
        private String username;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UserStats {
        private Long totalRuns;
        private Double totalDistanceKm;
        private Double avgPaceMinPerKm;
        private Double totalCalories;
    }
}
