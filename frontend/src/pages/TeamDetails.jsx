import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { teamService } from '../services/teamService';
import { memberService } from '../services/memberService';
import { useAuth } from '../context/AuthContext';
import { StatusBadge } from '../components/StatusBadge';
import { TrackBadge } from '../components/TrackBadge';
import { ConfirmModal } from '../components/ConfirmModal';
import { StatusTransitionModal } from '../components/StatusTransitionModal';
import { MemberModal } from '../components/MemberModal';
import {
  ArrowLeft, Edit3, Trash2, GitBranch, UserPlus, Users, Calendar, Award, AlertCircle
} from 'lucide-react';

export const TeamDetails = () => {
  const { id } = useParams();
  const [team, setTeam] = useState(null);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modals state
  const [statusModalOpen, setStatusModalOpen] = useState(false);
  const [deleteTeamModal, setDeleteTeamModal] = useState(false);
  const [memberModal, setMemberModal] = useState({ isOpen: false, member: null });
  const [deleteMemberModal, setDeleteMemberModal] = useState({ isOpen: false, memberId: null, memberName: '' });

  const { isAdmin, isOrganizer, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetchTeamAndMembers();
  }, [id]);

  const fetchTeamAndMembers = async () => {
    setLoading(true);
    setError('');
    try {
      const [teamData, membersData] = await Promise.all([
        teamService.getTeamById(id),
        memberService.getMembers(id)
      ]);
      setTeam(teamData);
      setMembers(membersData);
    } catch (err) {
      setError(err.extractedMessage || 'Failed to load team details.');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusTransition = async (targetStatus, remarks) => {
    await teamService.updateStatus(id, targetStatus, remarks);
    fetchTeamAndMembers();
  };

  const handleDeleteTeam = async () => {
    try {
      await teamService.deleteTeam(id);
      navigate('/teams');
    } catch (err) {
      setError(err.extractedMessage || 'Failed to delete team.');
    }
  };

  const handleSaveMember = async (memberData) => {
    if (memberModal.member) {
      await memberService.updateMember(id, memberModal.member.id, memberData);
    } else {
      await memberService.addMember(id, memberData);
    }
    fetchTeamAndMembers();
  };

  const handleDeleteMember = async () => {
    try {
      await memberService.deleteMember(id, deleteMemberModal.memberId);
      setDeleteMemberModal({ isOpen: false, memberId: null, memberName: '' });
      fetchTeamAndMembers();
    } catch (err) {
      setError(err.extractedMessage || 'Failed to remove team member.');
    }
  };

  const canEdit = isAdmin || isOrganizer || user?.username === team?.teamLead;

  if (loading) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem' }}>
        <p style={{ color: 'var(--text-secondary)' }}>Loading team profile...</p>
      </div>
    );
  }

  if (!team) {
    return (
      <div className="page-wrapper">
        <div className="alert alert-danger">Team record not found.</div>
        <Link to="/teams" className="btn btn-secondary">
          <ArrowLeft size={16} /> Back to Teams
        </Link>
      </div>
    );
  }

  return (
    <div className="page-wrapper" id="team-details-page">
      {/* Navigation Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <Link to="/teams" className="btn btn-secondary btn-sm" id="back-to-teams-btn">
          <ArrowLeft size={16} />
          <span>Back to Teams</span>
        </Link>

        <div style={{ display: 'flex', gap: '0.6rem' }}>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => setStatusModalOpen(true)}
            id="detail-status-btn"
          >
            <GitBranch size={16} color="var(--accent-primary)" />
            <span>Transition Status</span>
          </button>

          {canEdit && (
            <Link
              to={`/teams/${team.id}/edit`}
              className="btn btn-secondary btn-sm"
              id="detail-edit-btn"
            >
              <Edit3 size={16} />
              <span>Edit Details</span>
            </Link>
          )}

          {isAdmin && (
            <button
              type="button"
              className="btn btn-danger btn-sm"
              onClick={() => setDeleteTeamModal(true)}
              id="detail-delete-btn"
            >
              <Trash2 size={16} />
              <span>Delete</span>
            </button>
          )}
        </div>
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Main Team Info Banner */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', gap: '1.5rem', marginBottom: '1.5rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
              <code style={{ fontSize: '1rem', color: 'var(--accent-cyan)', fontWeight: 'bold' }} id="team-code-display">
                {team.teamCode}
              </code>
              <StatusBadge status={team.registrationStatus} />
              <TrackBadge track={team.track} />
            </div>
            <h2 style={{ fontSize: '2rem', fontWeight: '800' }} id="team-name-display">
              {team.teamName}
            </h2>
            <h4 style={{ color: 'var(--text-secondary)', fontWeight: '500', marginTop: '0.25rem' }}>
              {team.projectTitle}
            </h4>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem', textAlign: 'right' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Team Lead: <strong style={{ color: '#fff' }}>{team.teamLead}</strong>
            </span>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Registered: {new Date(team.createdAt).toLocaleDateString()}
            </span>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Last Updated: {new Date(team.updatedAt).toLocaleTimeString()}
            </span>
          </div>
        </div>

        <div>
          <h4 style={{ fontSize: '1rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
            Project Overview & Description
          </h4>
          <p style={{ color: 'var(--text-primary)', lineHeight: '1.6', background: 'var(--bg-tertiary)', padding: '1rem', borderRadius: 'var(--radius-md)' }}>
            {team.projectDescription}
          </p>
        </div>
      </div>

      {/* Team Roster Section */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <Users size={22} color="var(--accent-primary)" />
            <h3 style={{ fontSize: '1.25rem' }}>Team Roster ({members.length})</h3>
          </div>

          {canEdit && (
            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={() => setMemberModal({ isOpen: true, member: null })}
              id="add-member-btn"
            >
              <UserPlus size={16} />
              <span>Enroll Member</span>
            </button>
          )}
        </div>

        <div className="table-responsive">
          <table className="data-table" id="members-table">
            <thead>
              <tr>
                <th>Full Name</th>
                <th>Email</th>
                <th>College / Institution</th>
                <th>Department</th>
                <th>Role in Team</th>
                {canEdit && <th style={{ textAlign: 'right' }}>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {members.length === 0 ? (
                <tr>
                  <td colSpan={canEdit ? 6 : 5} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
                    No members enrolled yet. Click "Enroll Member" to add contributors.
                  </td>
                </tr>
              ) : (
                members.map((m) => (
                  <tr key={m.id} id={`member-row-${m.id}`}>
                    <td><strong style={{ color: '#fff' }}>{m.fullName}</strong></td>
                    <td>{m.email}</td>
                    <td>{m.college}</td>
                    <td>{m.department}</td>
                    <td>
                      <span style={{
                        padding: '2px 8px',
                        borderRadius: '4px',
                        background: 'rgba(99, 102, 241, 0.15)',
                        color: '#a5b4fc',
                        fontSize: '0.75rem',
                        fontWeight: '600'
                      }}>
                        {m.teamRole}
                      </span>
                    </td>
                    {canEdit && (
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.4rem' }}>
                          <button
                            type="button"
                            className="btn btn-secondary btn-sm"
                            title="Edit Member"
                            onClick={() => setMemberModal({ isOpen: true, member: m })}
                            id={`edit-member-${m.id}`}
                          >
                            <Edit3 size={14} />
                          </button>
                          <button
                            type="button"
                            className="btn btn-danger btn-sm"
                            title="Remove Member"
                            onClick={() => setDeleteMemberModal({
                              isOpen: true,
                              memberId: m.id,
                              memberName: m.fullName
                            })}
                            id={`delete-member-${m.id}`}
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modals */}
      <StatusTransitionModal
        isOpen={statusModalOpen}
        teamName={team.teamName}
        currentStatus={team.registrationStatus}
        onTransition={handleStatusTransition}
        onClose={() => setStatusModalOpen(false)}
      />

      <ConfirmModal
        isOpen={deleteTeamModal}
        title="Delete Team"
        message={`Are you sure you want to permanently delete "${team.teamName}"?`}
        confirmText="Delete Team"
        isDanger={true}
        onConfirm={handleDeleteTeam}
        onCancel={() => setDeleteTeamModal(false)}
      />

      <MemberModal
        isOpen={memberModal.isOpen}
        member={memberModal.member}
        onSave={handleSaveMember}
        onClose={() => setMemberModal({ isOpen: false, member: null })}
      />

      <ConfirmModal
        isOpen={deleteMemberModal.isOpen}
        title="Remove Member"
        message={`Are you sure you want to remove "${deleteMemberModal.memberName}" from the team roster?`}
        confirmText="Remove Member"
        isDanger={true}
        onConfirm={handleDeleteMember}
        onCancel={() => setDeleteMemberModal({ isOpen: false, memberId: null, memberName: '' })}
      />
    </div>
  );
};

export default TeamDetails;
