import { useEffect, useState } from 'react';
import api from '../api/axios';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/admin/users')
      .then(res => setUsers(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="spinner" />;

  return (
    <div>
      <h1 className="page-title">👥 회원 관리</h1>
      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>닉네임</th>
              <th>이메일</th>
              <th>나이</th>
              <th>몸무게</th>
              <th>키</th>
              <th>권한</th>
              <th>가입일</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td style={{ fontWeight: 600 }}>{u.username}</td>
                <td>{u.email}</td>
                <td>{u.age ?? '-'}</td>
                <td>{u.weight ? `${u.weight}kg` : '-'}</td>
                <td>{u.height ? `${u.height}cm` : '-'}</td>
                <td>
                  <span className={`badge badge-${u.role?.toLowerCase()}`}>
                    {u.role}
                  </span>
                </td>
                <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString('ko-KR') : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {users.length === 0 && (
          <p style={{ textAlign: 'center', color: '#aaa', padding: '40px' }}>회원이 없습니다.</p>
        )}
      </div>
    </div>
  );
}
