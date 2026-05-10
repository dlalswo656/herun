package com.herun.controller;

import com.herun.dto.RunRecordDto;
import com.herun.entity.RunRecord;
import com.herun.entity.User;
import com.herun.repository.RunRecordRepository;
import com.herun.repository.UserRepository;
import com.herun.service.RunRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RunRecordRepository runRecordRepository;
    private final RunRecordService runRecordService;

    // 전체 통계
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRuns", runRecordRepository.countAllRuns());
        stats.put("totalDistanceKm", runRecordRepository.sumAllDistance());
        stats.put("monthlyStats", runRecordRepository.getMonthlyStats());
        return ResponseEntity.ok(stats);
    }

    // 전체 회원 목록
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 전체 러닝 기록
    @GetMapping("/runs/all")
    public ResponseEntity<List<RunRecordDto.Response>> getAllRuns() {
        List<RunRecordDto.Response> result = runRecordRepository.findAll()
                .stream()
                .map(r -> new RunRecordDto.Response(
                        r.getId(), r.getDistanceKm(), r.getDurationSeconds(),
                        r.getPaceMinPerKm(), r.getCaloriesBurned(), r.getRouteJson(),
                        r.getStartLat(), r.getStartLng(), r.getRunDate(), r.getUser().getUsername()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
