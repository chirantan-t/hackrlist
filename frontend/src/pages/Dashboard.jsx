import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardService } from '../services/dashboardService';
import { StatCard } from '../components/StatCard';
import { StatusBadge } from '../components/StatusBadge';
import { TrackBadge } from '../components/TrackBadge';
import {
  Users, CheckCircle2, Clock, Send, Award, AlertCircle, FileText, PlusCircle, ArrowUpRight
} from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell, CartesianGrid
} from 'recharts';

export const Dashboard = () => {
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

  return (
    <div className="page-wrapper" id="dashboard-page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h2 style={{ fontSize: '1.75rem', fontWeight: '800' }}>Executive Dashboard</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Real-time telemetry and team lifecycle analytics
          </p>
        </div>
        <Link to="/teams/new" className="btn btn-primary" id="dashboard-register-btn">
          <PlusCircle size={18} />
          <span>Register New Team</span>
        </Link>
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
