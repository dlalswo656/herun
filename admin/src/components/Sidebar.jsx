import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { admin, logout } = useAuth();
  const { pathname } = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        HeRun
        <span>Admin Dashboard</span>
      </div>
      <nav className="sidebar-nav">
        <Link to="/" className={pathname === '/' ? 'active' : ''}>📊 대시보드</Link>
        <Link to="/users" className={pathname === '/users' ? 'active' : ''}>👥 회원 관리</Link>
        <Link to="/runs" className={pathname === '/runs' ? 'active' : ''}>🏃 러닝 기록</Link>
      </nav>
      <div className="sidebar-bottom">
        <div className="sidebar-user">👤 {admin?.username}</div>
        <button className="logout-btn" onClick={handleLogout}>로그아웃</button>
      </div>
    </aside>
  );
}
