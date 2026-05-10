import { useEffect, useState } from 'react';
import api from '../api/axios';

function formatDuration(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}분 ${s}초`;
}

function formatPace(pace) {
  if (!pace) return '-';
  const m = Math.floor(pace);
  const s = Math.round((pace - m) * 60);
  return `${m}'${String(s).padStart(2,'0')}"`;
}

export default function Runs() {
  const [runs, setRuns] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/runs/all')
      .then(res => setRuns(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <h1 className="page-title">🏃 러닝 기록</h1>
      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>회원</th>
              <th>거리</th>
              <th>시간</th>
              <th>페이스</th>
              <th>칼로리</th>
              <th>날짜</th>
            </tr>
          </thead>
          <tbody>
            {runs.map(r => (
              <tr key={r.id}>
                <td>{r.id}</td>
                <td style={{ fontWeight: 600 }}>{r.username}</td>
                <td>{r.distanceKm?.toFixed(2)} km</td>
                <td>{formatDuration(r.durationSeconds)}</td>
                <td>{formatPace(r.paceMinPerKm)}/km</td>
                <td>{r.caloriesBurned ? `${r.caloriesBurned.toFixed(0)} kcal` : '-'}</td>
                <td>{r.runDate ? new Date(r.runDate).toLocaleDateString('ko-KR') : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {runs.length === 0 && (
          <p style={{ textAlign: 'center', color: '#aaa', padding: '40px' }}>러닝 기록이 없습니다.</p>
        )}
      </div>
    </div>
  );
}
