import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { teamService } from '../services/teamService';
import { useAuth } from '../context/AuthContext';
import { StatusBadge } from '../components/StatusBadge';
import { TrackBadge } from '../components/TrackBadge';
import { ConfirmModal } from '../components/ConfirmModal';
import { StatusTransitionModal } from '../components/StatusTransitionModal';
import {
  Search, Filter, PlusCircle, Trash2, Edit3, GitBranch, Eye, AlertCircle, RefreshCw
} from 'lucide-react';

export const TeamList = () => {
  const [teams, setTeams] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [selectedTrack, setSelectedTrack] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Modals state
  const [deleteModal, setDeleteModal] = useState({ isOpen: false, teamId: null, teamName: '' });
  const [statusModal, setStatusModal] = useState({ isOpen: false, teamId: null, teamName: '', currentStatus: '' });

  const { isAdmin, isOrganizer, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetchTeams();
  }, [currentPage, selectedStatus, selectedTrack]);

  const fetchTeams = async () => {
    setLoading(true);
    setError('');
    try {
      const params = {
        page: currentPage,
        size: 8,
        status: selectedStatus || undefined,
        track: selectedTrack || undefined
      };

      let data;
      if (searchQuery.trim()) {
        data = await teamService.searchTeams(searchQuery.trim(), params);
      } else {
        data = await teamService.getTeams(params);
      }

      setTeams(data.content || []);
      setTotalElements(data.totalElements || 0);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      setError(err.extractedMessage || 'Failed to fetch teams.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setCurrentPage(0);
    fetchTeams();
  };

  const handleClearFilters = () => {
    setSearchQuery('');
    setSelectedStatus('');
    setSelectedTrack('');
    setCurrentPage(0);
  };

  const handleDeleteConfirm = async () => {
    try {
      await teamService.deleteTeam(deleteModal.teamId);
      setDeleteModal({ isOpen: false, teamId: null, teamName: '' });
      fetchTeams();
    } catch (err) {
      setError(err.extractedMessage || 'Failed to delete team.');
    }
  };

  const handleStatusTransitionConfirm = async (targetStatus, remarks) => {
    await teamService.updateStatus(statusModal.teamId, targetStatus, remarks);
    fetchTeams();
  };

  return (
    <div className="page-wrapper" id="team-list-page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h2 style={{ fontSize: '1.75rem', fontWeight: '800' }}>Hackathon Teams</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Browse, search, filter, and transition team progress across the event lifecycle
          </p>
        </div>
        <Link to="/teams/new" className="btn btn-primary" id="btn-create-team">
          <PlusCircle size={18} />
          <span>Register Team</span>
        </Link>
      </div>

      {error && (
        <div className="alert alert-danger" id="teams-alert-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Filter & Search Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem 1.25rem' }}>
        <form onSubmit={handleSearchSubmit} className="filter-bar">
          <div className="search-input-wrapper">
            <input
              id="search-input"
              type="text"
              className="form-control"
              placeholder="Search by team name, code, or project title..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div style={{ minWidth: '160px' }}>
            <select
              id="status-filter"
              className="form-control"
              value={selectedStatus}
              onChange={(e) => { setSelectedStatus(e.target.value); setCurrentPage(0); }}
            >
              <option value="">All Statuses</option>
              <option value="DRAFT">Draft</option>
              <option value="REGISTERED">Registered</option>
              <option value="APPROVED">Approved</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="SUBMITTED">Submitted</option>
              <option value="COMPLETED">Completed</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div style={{ minWidth: '180px' }}>
            <select
              id="track-filter"
              className="form-control"
              value={selectedTrack}
              onChange={(e) => { setSelectedTrack(e.target.value); setCurrentPage(0); }}
            >
              <option value="">All Tracks</option>
              <option value="AI_ML">AI & Machine Learning</option>
              <option value="WEB3_BLOCKCHAIN">Web3 & Blockchain</option>
              <option value="FINTECH">FinTech</option>
              <option value="HEALTHTECH">HealthTech</option>
              <option value="OPEN_INNOVATION">Open Innovation</option>
              <option value="CYBERSECURITY">Cybersecurity</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary btn-sm" id="search-submit-btn">
            <Search size={16} />
            <span>Search</span>
          </button>

          {(searchQuery || selectedStatus || selectedTrack) && (
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={handleClearFilters}
              id="clear-filters-btn"
            >
              <RefreshCw size={14} />
              <span>Reset</span>
            </button>
          )}
        </form>
      </div>

      {/* Teams Table */}
      <div className="card">
        <div className="table-responsive">
          <table className="data-table" id="teams-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Team Name</th>
                <th>Project Title</th>
                <th>Team Lead</th>
                <th>Track</th>
                <th>Status</th>
                <th>Members</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                    Loading team records...
                  </td>
                </tr>
              ) : teams.length === 0 ? (
                <tr>
                  <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }} id="no-teams-message">
                    No hackathon teams match the current criteria.
                  </td>
                </tr>
              ) : (
                teams.map((team) => (
                  <tr key={team.id} id={`team-row-${team.id}`} data-team-code={team.teamCode}>
                    <td>
                      <code style={{ color: 'var(--accent-cyan)', fontWeight: 'bold' }}>
                        {team.teamCode}
                      </code>
                    </td>
                    <td>
                      <strong style={{ color: '#fff' }} className="team-name-cell">
                        {team.teamName}
                      </strong>
                    </td>
                    <td style={{ maxWidth: '240px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {team.projectTitle}
                    </td>
                    <td>{team.teamLead}</td>
                    <td><TrackBadge track={team.track} /></td>
                    <td><StatusBadge status={team.registrationStatus} /></td>
                    <td>
                      <span style={{
                        display: 'inline-block',
                        padding: '2px 8px',
                        background: 'rgba(255,255,255,0.06)',
                        borderRadius: 'var(--radius-sm)',
                        fontWeight: '600'
                      }}>
                        {team.memberCount}
                      </span>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                        <Link
                          to={`/teams/${team.id}`}
                          className="btn btn-secondary btn-sm"
                          title="View Details"
                          id={`view-team-${team.id}`}
                        >
                          <Eye size={14} />
                        </Link>

                        {(isAdmin || isOrganizer || user?.username === team.teamLead) && (
                          <Link
                            to={`/teams/${team.id}/edit`}
                            className="btn btn-secondary btn-sm"
                            title="Edit Team"
                            id={`edit-team-${team.id}`}
                          >
                            <Edit3 size={14} />
                          </Link>
                        )}

                        <button
                          type="button"
                          className="btn btn-secondary btn-sm"
                          title="Transition Status"
                          id={`status-team-${team.id}`}
                          onClick={() => setStatusModal({
                            isOpen: true,
                            teamId: team.id,
                            teamName: team.teamName,
                            currentStatus: team.registrationStatus
                          })}
                        >
                          <GitBranch size={14} color="var(--accent-primary)" />
                        </button>

                        {isAdmin && (
                          <button
                            type="button"
                            className="btn btn-danger btn-sm"
                            title="Delete Team"
                            id={`delete-team-${team.id}`}
                            onClick={() => setDeleteModal({
                              isOpen: true,
                              teamId: team.id,
                              teamName: team.teamName
                            })}
                          >
                            <Trash2 size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        {totalPages > 1 && (
          <div className="pagination">
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Showing Page {currentPage + 1} of {totalPages} ({totalElements} total teams)
            </span>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
                id="prev-page-btn"
              >
                Previous
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage(p => p + 1)}
                id="next-page-btn"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Confirmation & Status Transition Modals */}
      <ConfirmModal
        isOpen={deleteModal.isOpen}
        title="Delete Hackathon Team"
        message={`Are you sure you want to permanently delete "${deleteModal.teamName}"? This will delete all team records and unenroll all members.`}
        confirmText="Delete Team"
        isDanger={true}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteModal({ isOpen: false, teamId: null, teamName: '' })}
      />

      <StatusTransitionModal
        isOpen={statusModal.isOpen}
        teamName={statusModal.teamName}
        currentStatus={statusModal.currentStatus}
        onTransition={handleStatusTransitionConfirm}
        onClose={() => setStatusModal({ isOpen: false, teamId: null, teamName: '', currentStatus: '' })}
      />
    </div>
  );
};

export default TeamList;
