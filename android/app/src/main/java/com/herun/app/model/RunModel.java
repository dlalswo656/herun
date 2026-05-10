package com.herun.app.model;

public class RunModel {

    public static class SaveRequest {
        public Double distanceKm;
        public Integer durationSeconds;
        public Double paceMinPerKm;
        public Double caloriesBurned;
        public String routeJson;
        public Double startLat;
        public Double startLng;

        public SaveRequest(Double distanceKm, Integer durationSeconds, Double paceMinPerKm,
                           Double caloriesBurned, String routeJson, Double startLat, Double startLng) {
            this.distanceKm = distanceKm;
            this.durationSeconds = durationSeconds;
            this.paceMinPerKm = paceMinPerKm;
            this.caloriesBurned = caloriesBurned;
            this.routeJson = routeJson;
            this.startLat = startLat;
            this.startLng = startLng;
        }
    }

    public static class Response {
        public Long id;
        public Double distanceKm;
        public Integer durationSeconds;
        public Double paceMinPerKm;
        public Double caloriesBurned;
        public String routeJson;
        public Double startLat;
        public Double startLng;
        public String runDate;
        public String username;
    }

    public static class UserStats {
        public Long totalRuns;
        public Double totalDistanceKm;
        public Double avgPaceMinPerKm;
        public Double totalCalories;
    }
}
