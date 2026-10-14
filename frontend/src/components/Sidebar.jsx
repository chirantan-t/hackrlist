import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, Users, UserPlus, LogOut, Shield, Terminal } from 'lucide-react';

export const Sidebar = () => {
  const { user, logout, role } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="sidebar-logo-icon">
          <Terminal size={20} />
        </div>
        <div className="sidebar-brand">
          <span style={{ color: 'var(--accent-primary)' }}>hackr</span>base
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink
          to="/"
          id="nav-dashboard"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </NavLink>

        <NavLink
          to="/teams"
          id="nav-teams"
          end
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <Users size={18} />
          <span>Hackathon Teams</span>
        </NavLink>

        <NavLink
          to="/teams/new"
          id="nav-create-team"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <UserPlus size={18} />
          <span>Register Team</span>
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="user-card">
          <div className="user-info">
            <span className="user-name" title={user?.fullName || user?.username}>
              {user?.fullName || user?.username}
            </span>
            <span className="user-role-badge">
              {role || 'GUEST'}
            </span>
          </div>
          <button
            id="logout-button"
            className="btn btn-secondary btn-sm"
            onClick={handleLogout}
            title="Log Out"
            style={{ padding: '0.4rem', border: 'none', background: 'transparent' }}
          >
            <LogOut size={16} color="var(--accent-rose)" />
          </button>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
