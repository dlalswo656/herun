package com.herun.service;

import com.herun.dto.RunRecordDto;
import com.herun.entity.RunRecord;
import com.herun.entity.User;
import com.herun.repository.RunRecordRepository;
import com.herun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunRecordService {

    private final RunRecordRepository runRecordRepository;
    private final UserRepository userRepository;

    public RunRecordDto.Response saveRun(String email, RunRecordDto.SaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        RunRecord record = RunRecord.builder()
                .user(user)
                .distanceKm(request.getDistanceKm())
                .durationSeconds(request.getDurationSeconds())
                .paceMinPerKm(request.getPaceMinPerKm())
                .caloriesBurned(request.getCaloriesBurned())
                .routeJson(request.getRouteJson())
                .startLat(request.getStartLat())
                .startLng(request.getStartLng())
                .build();

        runRecordRepository.save(record);
        return toResponse(record);
    }

    public List<RunRecordDto.Response> getMyRuns(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return runRecordRepository.findByUserIdOrderByRunDateDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RunRecordDto.UserStats getMyStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Long totalRuns = runRecordRepository.countByUserId(user.getId());
        Double totalDist = runRecordRepository.sumDistanceByUserId(user.getId());
        List<RunRecord> records = runRecordRepository.findByUserIdOrderByRunDateDesc(user.getId());

        double avgPace = records.stream()
                .filter(r -> r.getPaceMinPerKm() != null)
                .mapToDouble(RunRecord::getPaceMinPerKm)
                .average().orElse(0);

        double totalCal = records.stream()
                .filter(r -> r.getCaloriesBurned() != null)
                .mapToDouble(RunRecord::getCaloriesBurned)
                .sum();

        return new RunRecordDto.UserStats(totalRuns, totalDist, avgPace, totalCal);
    }

    private RunRecordDto.Response toResponse(RunRecord r) {
        return new RunRecordDto.Response(
                r.getId(), r.getDistanceKm(), r.getDurationSeconds(),
                r.getPaceMinPerKm(), r.getCaloriesBurned(), r.getRouteJson(),
                r.getStartLat(), r.getStartLng(), r.getRunDate(), r.getUser().getUsername()
        );
    }
}
