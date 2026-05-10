package com.herun.controller;

import com.herun.dto.RunRecordDto;
import com.herun.service.RunRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/runs")
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordService;

    // 러닝 기록 저장
    @PostMapping
    public ResponseEntity<RunRecordDto.Response> saveRun(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RunRecordDto.SaveRequest request) {
        return ResponseEntity.ok(runRecordService.saveRun(userDetails.getUsername(), request));
    }

    // 내 러닝 기록 목록
    @GetMapping("/my")
    public ResponseEntity<List<RunRecordDto.Response>> getMyRuns(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(runRecordService.getMyRuns(userDetails.getUsername()));
    }

    // 내 통계
    @GetMapping("/my/stats")
    public ResponseEntity<RunRecordDto.UserStats> getMyStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(runRecordService.getMyStats(userDetails.getUsername()));
    }
}
