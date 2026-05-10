import { useEffect, useState } from 'react';
import api from '../api/axios';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  LineChart, Line, Legend
} from 'recharts';

const MONTHS = ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'];

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/stats')
      .then(res => setStats(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner" />;

  const monthlyData = (stats?.monthlyStats || []).map(([month, count, dist]) => ({
    name: MONTHS[month - 1],
    러닝횟수: Number(count),
    총거리: Number(dist).toFixed(1),
  }));

  return (
    <div>
      <h1 className="page-title">📊 대시보드</h1>

      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-label">전체 회원</div>
          <div className="stat-value">{stats?.totalUsers ?? 0}<span className="stat-unit">명</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-label">전체 러닝</div>
          <div className="stat-value">{stats?.totalRuns ?? 0}<span className="stat-unit">회</span></div>
        </div>
        <div className="stat-card">
          <div className="stat-label">총 누적 거리</div>
          <div className="stat-value">
            {Number(stats?.totalDistanceKm ?? 0).toFixed(1)}<span className="stat-unit">km</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-label">평균 거리/회</div>
          <div className="stat-value">
            {stats?.totalRuns > 0
              ? (Number(stats?.totalDistanceKm) / Number(stats?.totalRuns)).toFixed(2)
              : 0}
            <span className="stat-unit">km</span>
          </div>
        </div>
      </div>

      <div className="chart-card">
        <div className="chart-title">📈 월별 러닝 횟수</div>
        <ResponsiveContainer width="100%" height={280}>
          <BarChart data={monthlyData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="name" tick={{ fontSize: 13 }} />
            <YAxis tick={{ fontSize: 13 }} />
            <Tooltip />
            <Bar dataKey="러닝횟수" fill="#4CAF50" radius={[4,4,0,0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      <div className="chart-card">
        <div className="chart-title">🏃 월별 총 거리 (km)</div>
        <ResponsiveContainer width="100%" height={280}>
          <LineChart data={monthlyData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="name" tick={{ fontSize: 13 }} />
            <YAxis tick={{ fontSize: 13 }} />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="총거리" stroke="#4CAF50" strokeWidth={2} dot={{ r: 4 }} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
