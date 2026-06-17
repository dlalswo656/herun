import { useEffect, useState } from 'react';
import api from '../api/axios';

const ADMIN_EMAIL = 'dlalswo656@gmail.com';

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editUser, setEditUser] = useState(null); // 수정 모달용

  const fetchUsers = () => {
    api.get('/admin/users')
      .then(res => setUsers(res.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleDelete = async (id, username) => {
    if (!window.confirm(`"${username}" 회원을 삭제하시겠어요?`)) return;
    try {
      await api.delete(`/admin/users/${id}`);
      setUsers(prev => prev.filter(u => u.id !== id));
    } catch {
      alert('삭제 실패');
    }
  };

  const handleRoleChange = async (id, currentRole) => {
    const newRole = currentRole === 'ADMIN' ? 'USER' : 'ADMIN';
    if (!window.confirm(`권한을 ${newRole}으로 변경하시겠어요?`)) return;
    try {
      await api.put(`/admin/users/${id}/role`, { role: newRole });
      setUsers(prev => prev.map(u => u.id === id ? { ...u, role: newRole } : u));
    } catch {
      alert('권한 변경 실패');
    }
  };

  const handleEditSave = async () => {
    try {
      await api.put(`/admin/users/${editUser.id}`, {
        username: editUser.username,
        age: editUser.age || null,
        weight: editUser.weight || null,
        height: editUser.height || null,
      });
      setUsers(prev => prev.map(u => u.id === editUser.id ? { ...u, ...editUser } : u));
      setEditUser(null);
    } catch {
      alert('수정 실패');
    }
  };

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
              <th>관리</th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td style={{ fontWeight: 600 }}>{u.username}</td>
                <td>{u.email.replace(/(.{2}).*(@.*)/, '$1***$2')}</td>
                <td>{u.age ?? '-'}</td>
                <td>{u.weight ? `${u.weight}kg` : '-'}</td>
                <td>{u.height ? `${u.height}cm` : '-'}</td>
                <td>
                  <span className={`badge badge-${u.role?.toLowerCase()}`}>{u.role}</span>
                </td>
                <td>{u.createdAt ? new Date(u.createdAt).toLocaleDateString('ko-KR') : '-'}</td>
                <td>
                  {u.email === ADMIN_EMAIL ? (
                    <span style={{ color: '#aaa', fontSize: '0.8rem' }}>최고 관리자</span>
                  ) : (
                    <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                      <button onClick={() => setEditUser({ ...u })}
                        style={{ padding: '4px 10px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '0.75rem', fontWeight: 600, background: '#fff8e1', color: '#e65100' }}>
                        수정
                      </button>
                      <button onClick={() => handleRoleChange(u.id, u.role)}
                        style={{ padding: '4px 10px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '0.75rem', fontWeight: 600, background: u.role === 'ADMIN' ? '#e3f2fd' : '#e8f5e9', color: u.role === 'ADMIN' ? '#1565c0' : '#2e7d32' }}>
                        {u.role === 'ADMIN' ? '→ USER' : '→ ADMIN'}
                      </button>
                      <button onClick={() => handleDelete(u.id, u.username)}
                        style={{ padding: '4px 10px', borderRadius: '6px', border: 'none', cursor: 'pointer', fontSize: '0.75rem', fontWeight: 600, background: '#fdecea', color: '#c62828' }}>
                        삭제
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {users.length === 0 && (
          <p style={{ textAlign: 'center', color: '#aaa', padding: '40px' }}>회원이 없습니다.</p>
        )}
      </div>

      {/* 수정 모달 */}
      {editUser && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
          background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{ background: '#fff', borderRadius: '16px', padding: '32px', width: '420px', boxShadow: '0 20px 60px rgba(0,0,0,0.3)' }}>
            <h2 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '24px', color: '#1a1a2e' }}>
              회원 정보 수정
            </h2>

            {[
              { label: '닉네임', key: 'username', type: 'text' },
              { label: '나이', key: 'age', type: 'number' },
              { label: '몸무게 (kg)', key: 'weight', type: 'number' },
              { label: '키 (cm)', key: 'height', type: 'number' },
            ].map(({ label, key, type }) => (
              <div key={key} style={{ marginBottom: '16px' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', color: '#666', marginBottom: '6px', fontWeight: 600 }}>
                  {label}
                </label>
                <input
                  type={type}
                  value={editUser[key] ?? ''}
                  onChange={e => setEditUser({ ...editUser, [key]: e.target.value })}
                  style={{ width: '100%', padding: '10px 14px', border: '1px solid #ddd', borderRadius: '8px', fontSize: '0.95rem', outline: 'none' }}
                />
              </div>
            ))}

            <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
              <button onClick={handleEditSave}
                style={{ flex: 1, padding: '12px', background: '#4CAF50', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: 700, cursor: 'pointer', fontSize: '0.95rem' }}>
                저장
              </button>
              <button onClick={() => setEditUser(null)}
                style={{ flex: 1, padding: '12px', background: '#f5f5f5', color: '#666', border: 'none', borderRadius: '8px', fontWeight: 700, cursor: 'pointer', fontSize: '0.95rem' }}>
                취소
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
