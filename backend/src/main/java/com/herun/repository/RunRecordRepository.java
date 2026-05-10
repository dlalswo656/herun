package com.herun.repository;

import com.herun.entity.RunRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RunRecordRepository extends JpaRepository<RunRecord, Long> {

    List<RunRecord> findByUserIdOrderByRunDateDesc(Long userId);

    // 총 통계 (관리자용)
    @Query("SELECT COUNT(r) FROM RunRecord r")
    Long countAllRuns();

    @Query("SELECT COALESCE(SUM(r.distanceKm), 0) FROM RunRecord r")
    Double sumAllDistance();

    // 유저별 통계
    @Query("SELECT COALESCE(SUM(r.distanceKm), 0) FROM RunRecord r WHERE r.user.id = :userId")
    Double sumDistanceByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM RunRecord r WHERE r.user.id = :userId")
    Long countByUserId(Long userId);

    // 월별 통계 (관리자)
    @Query("SELECT MONTH(r.runDate), COUNT(r), COALESCE(SUM(r.distanceKm), 0) " +
           "FROM RunRecord r GROUP BY MONTH(r.runDate) ORDER BY MONTH(r.runDate)")
    List<Object[]> getMonthlyStats();
}
