import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { dashboardService } from '../services/dashboardService';
import { StatCard } from '../components/StatCard';
import { StatusBadge } from '../components/StatusBadge';
import { TrackBadge } from '../components/TrackBadge';
import {
  Users, CheckCircle2, Clock, Send, Award, AlertCircle, FileText, PlusCircle, ArrowUpRight,
  ShieldCheck, Compass, Code2, Sparkles, UserCheck
} from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell, CartesianGrid
} from 'recharts';

export const Dashboard = () => {
  const { user, isAdmin, isOrganizer, isLead } = useAuth();
  const [summary, setSummary] = useState(null);
  const [statusData, setStatusData] = useState([]);
  const [trackData, setTrackData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [sum, stDist, trDist] = await Promise.all([
        dashboardService.getSummary(),
        dashboardService.getStatusDistribution(),
        dashboardService.getTrackDistribution()
      ]);
      setSummary(sum);
      setStatusData(stDist);
      setTrackData(trDist);
    } catch (err) {
      setError('Failed to load dashboard metrics from backend.');
    } finally {
      setLoading(false);
    }
  };

  const statusColors = ['#9ca3af', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4', '#f43f5e'];
  const trackColors = ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6'];

  if (loading) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem' }}>
        <p style={{ color: 'var(--text-secondary)' }}>Loading live dashboard statistics...</p>
      </div>
    );
  }

  // Role-specific display configurations
  const roleConfig = isAdmin ? {
    badge: 'Administrator Control Panel',
    icon: ShieldCheck,
    badgeColor: '#ef4444',
    badgeBg: 'rgba(239, 68, 68, 0.12)',
    title: 'Executive Administrator Dashboard',
    subtitle: 'System-wide governance, unrestricted team lifecycle oversight, and platform audit metrics',
    bannerText: 'Full Privileged Access: Authorized to perform cascade deletion, database configuration, and global state machine overrides.'
  } : isOrganizer ? {
    badge: 'Organizer Operations Desk',
    icon: Compass,
    badgeColor: '#f59e0b',
    badgeBg: 'rgba(245, 158, 11, 0.12)',
    title: 'Hackathon Operations & Review Hub',
    subtitle: 'Manage team approvals, monitor submission deadlines, and evaluate category track allocations',
    bannerText: 'Organizer Scope: Authorized to review candidate teams, progress workflow states, and manage track allocations.'
  } : {
    badge: 'Participant & Team Lead Portal',
    icon: Code2,
    badgeColor: '#38bdf8',
    badgeBg: 'rgba(56, 189, 248, 0.12)',
    title: 'Team Lead Project Dashboard',
    subtitle: 'Track event milestones, oversee your project team roster, and prepare final hackathon deliverables',
    bannerText: `Welcome, ${user?.fullName || user?.username || 'Team Lead'}! Monitor your team's hackathon progress, manage members, and submit your project.`
  };

  const RoleIcon = roleConfig.icon;

  return (
    <div className="page-wrapper" id="dashboard-page">
      {/* Role-Specific Header Banner */}
      <div style={{
        background: 'var(--card-bg)',
        border: '1px solid var(--border-subtle)',
        borderRadius: '12px',
        padding: '1.25rem 1.5rem',
        marginBottom: '1.75rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexWrap: 'wrap',
        gap: '1rem'
      }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.4rem' }}>
            <span style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.35rem',
              padding: '0.25rem 0.75rem',
              borderRadius: '9999px',
              fontSize: '0.75rem',
              fontWeight: '700',
              textTransform: 'uppercase',
              letterSpacing: '0.05em',
              background: roleConfig.badgeBg,
              color: roleConfig.badgeColor,
              border: `1px solid ${roleConfig.badgeColor}33`
            }}>
              <RoleIcon size={14} />
              {roleConfig.badge}
            </span>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              Logged in as <strong style={{ color: 'var(--text-primary)' }}>{user?.fullName || user?.username}</strong>
            </span>
          </div>
          <h2 style={{ fontSize: '1.65rem', fontWeight: '800', margin: '0.25rem 0' }}>{roleConfig.title}</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', margin: 0 }}>
            {roleConfig.subtitle}
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
          <Link to="/teams" className="btn btn-secondary btn-sm" id="dashboard-view-teams-btn">
            <Users size={16} />
            <span>Browse All Teams</span>
          </Link>
          <Link to="/teams/new" className="btn btn-primary btn-sm" id="dashboard-register-btn">
            <PlusCircle size={16} />
            <span>Register Team</span>
          </Link>
        </div>
      </div>

      {/* Role Notice Banner */}
      <div style={{
        background: roleConfig.badgeBg,
        border: `1px solid ${roleConfig.badgeColor}33`,
        borderRadius: '8px',
        padding: '0.75rem 1rem',
        marginBottom: '1.5rem',
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        fontSize: '0.85rem',
        color: 'var(--text-primary)'
      }}>
        <Sparkles size={18} style={{ color: roleConfig.badgeColor, flexShrink: 0 }} />
        <span>{roleConfig.bannerText}</span>
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* KPI Stat Cards */}
      <div className="stat-grid">
        <StatCard
          id="stat-total-teams"
          label="Total Teams"
          value={summary?.totalTeams || 0}
          icon={Users}
          color="var(--accent-primary)"
        />
        <StatCard
          id="stat-registered-teams"
          label="Registered"
          value={summary?.registeredTeams || 0}
          icon={FileText}
          color="#3b82f6"
        />
        <StatCard
          id="stat-approved-teams"
          label="Approved"
          value={summary?.approvedTeams || 0}
          icon={CheckCircle2}
          color="var(--accent-emerald)"
        />
        <StatCard
          id="stat-in-progress-teams"
          label="In Progress"
          value={summary?.inProgressTeams || 0}
          icon={Clock}
          color="var(--accent-amber)"
        />
        <StatCard
          id="stat-submitted-teams"
          label="Submitted"
          value={summary?.submittedTeams || 0}
          icon={Send}
          color="var(--accent-purple)"
        />
        <StatCard
          id="stat-completed-teams"
          label="Completed"
          value={summary?.completedTeams || 0}
          icon={Award}
          color="var(--accent-cyan)"
        />
        <StatCard
          id="stat-total-members"
          label="Active Members"
          value={summary?.totalMembers || 0}
          icon={Users}
          color="#a5b4fc"
        />
      </div>

      {/* Analytics Charts Grid */}
      <div className="charts-grid">
        {/* Status Distribution Chart */}
        <div className="card">
          <div className="chart-header">
            <h3 style={{ fontSize: '1.15rem' }}>Team Lifecycle Progression</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Distribution across stages</span>
          </div>
          <div style={{ height: 280, width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={statusData} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
                <XAxis
                  dataKey="label"
                  stroke="var(--text-muted)"
                  fontSize={11}
                  interval={0}
                  angle={-20}
                  textAnchor="end"
                />
                <YAxis stroke="var(--text-muted)" fontSize={12} allowDecimals={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'var(--bg-secondary)',
                    borderColor: 'var(--border-subtle)',
                    borderRadius: 'var(--radius-md)',
                    color: '#fff'
                  }}
                />
                <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                  {statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={statusColors[index % statusColors.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Track Distribution Chart */}
        <div className="card">
          <div className="chart-header">
            <h3 style={{ fontSize: '1.15rem' }}>Hackathon Tracks Breakdown</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Team distribution by focus area</span>
          </div>
          <div style={{ height: 280, width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={trackData} margin={{ top: 10, right: 10, left: -20, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
                <XAxis
                  dataKey="label"
                  stroke="var(--text-muted)"
                  fontSize={11}
                  interval={0}
                  angle={-20}
                  textAnchor="end"
                />
                <YAxis stroke="var(--text-muted)" fontSize={12} allowDecimals={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'var(--bg-secondary)',
                    borderColor: 'var(--border-subtle)',
                    borderRadius: 'var(--radius-md)',
                    color: '#fff'
                  }}
                />
                <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                  {trackData.map((entry, index) => (
                    <Cell key={`cell-tr-${index}`} fill={trackColors[index % trackColors.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Recent Teams Activity Table */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
          <div>
            <h3 style={{ fontSize: '1.15rem' }}>Recently Updated Teams</h3>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Latest submissions and roster modifications</span>
          </div>
          <Link to="/teams" style={{ color: 'var(--accent-primary)', fontSize: '0.85rem', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            <span>View All</span>
            <ArrowUpRight size={14} />
          </Link>
        </div>

        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Team Name</th>
                <th>Project Title</th>
                <th>Lead</th>
                <th>Track</th>
                <th>Status</th>
                <th>Members</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {summary?.recentTeams?.length === 0 ? (
                <tr>
                  <td colSpan="8" style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '2rem' }}>
                    No team activity logged yet.
                  </td>
                </tr>
              ) : (
                summary?.recentTeams?.map((t) => (
                  <tr key={t.id}>
                    <td><code>{t.teamCode}</code></td>
                    <td><strong style={{ color: '#fff' }}>{t.teamName}</strong></td>
                    <td>{t.projectTitle}</td>
                    <td>{t.teamLead}</td>
                    <td><TrackBadge track={t.track} /></td>
                    <td><StatusBadge status={t.registrationStatus} /></td>
                    <td>{t.memberCount}</td>
                    <td>
                      <Link to={`/teams/${t.id}`} className="btn btn-secondary btn-sm">
                        View
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
