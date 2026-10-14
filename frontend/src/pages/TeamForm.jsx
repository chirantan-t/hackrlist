import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { teamService } from '../services/teamService';
import { useAuth } from '../context/AuthContext';
import { ArrowLeft, Save, AlertCircle, Sparkles } from 'lucide-react';

const tracks = [
  { value: 'AI_ML', label: 'AI & Machine Learning' },
  { value: 'WEB3_BLOCKCHAIN', label: 'Web3 & Blockchain' },
  { value: 'FINTECH', label: 'FinTech & DeFi' },
  { value: 'HEALTHTECH', label: 'HealthTech & Bio' },
  { value: 'OPEN_INNOVATION', label: 'Open Innovation' },
  { value: 'CYBERSECURITY', label: 'Cybersecurity' }
];

export const TeamForm = () => {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const { user } = useAuth();

  const [formData, setFormData] = useState({
    teamName: '',
    teamCode: '',
    projectTitle: '',
    projectDescription: '',
    teamLead: user?.username || '',
    track: 'AI_ML'
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(isEdit);

  useEffect(() => {
    if (isEdit) {
      teamService.getTeamById(id)
        .then(t => {
          setFormData({
            teamName: t.teamName,
            teamCode: t.teamCode,
            projectTitle: t.projectTitle,
            projectDescription: t.projectDescription,
            teamLead: t.teamLead,
            track: t.track
          });
        })
        .catch(err => {
          setError(err.extractedMessage || 'Failed to load team data.');
        })
        .finally(() => setFetching(false));
    }
  }, [id, isEdit]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isEdit) {
        await teamService.updateTeam(id, {
          teamName: formData.teamName,
          projectTitle: formData.projectTitle,
          projectDescription: formData.projectDescription,
          teamLead: formData.teamLead,
          track: formData.track
        });
        navigate(`/teams/${id}`);
      } else {
        const created = await teamService.createTeam(formData);
        navigate(`/teams/${created.id}`);
      }
    } catch (err) {
      setError(err.extractedMessage || 'Failed to save team.');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateCode = () => {
    const randomCode = 'HTM-' + Math.random().toString(36).substring(2, 6).toUpperCase();
    setFormData(prev => ({ ...prev, teamCode: randomCode }));
  };

  if (fetching) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem' }}>
        <p style={{ color: 'var(--text-secondary)' }}>Loading team data...</p>
      </div>
    );
  }

  return (
    <div className="page-wrapper" id="team-form-page" style={{ maxWidth: '800px' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <Link to="/teams" className="btn btn-secondary btn-sm" id="form-back-btn">
          <ArrowLeft size={16} />
          <span>Back to Teams</span>
        </Link>
      </div>

      <div className="card">
        <div style={{ marginBottom: '1.5rem', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '1rem' }}>
          <h2 style={{ fontSize: '1.5rem', fontWeight: '800' }}>
            {isEdit ? 'Update Team Information' : 'Register New Hackathon Team'}
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            {isEdit
              ? 'Modify project specifications and track allocations'
              : 'Submit official project details to enroll into the hackathon lifecycle'}
          </p>
        </div>

        {error && (
          <div className="alert alert-danger" id="team-form-error">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
            <div className="form-group">
              <label className="form-label" htmlFor="team-name-input">
                Team Name *
              </label>
              <input
                id="team-name-input"
                className="form-control"
                type="text"
                placeholder="e.g. Neural Hackers"
                value={formData.teamName}
                onChange={(e) => setFormData({ ...formData, teamName: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="team-code-input">
                Unique Team Code *
              </label>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <input
                  id="team-code-input"
                  className="form-control"
                  type="text"
                  placeholder="e.g. HTM-AI-01"
                  value={formData.teamCode}
                  onChange={(e) => setFormData({ ...formData, teamCode: e.target.value.toUpperCase() })}
                  disabled={isEdit}
                  required
                />
                {!isEdit && (
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={handleGenerateCode}
                    title="Generate Random Code"
                    id="gen-code-btn"
                  >
                    <Sparkles size={16} />
                  </button>
                )}
              </div>
              <small style={{ color: 'var(--text-muted)', display: 'block', marginTop: '0.25rem' }}>
                {isEdit ? 'Team codes cannot be altered after creation' : 'Must be unique across the event'}
              </small>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="project-title-input">
              Project Title *
            </label>
            <input
              id="project-title-input"
              className="form-control"
              type="text"
              placeholder="e.g. Autonomous Vision Navigation for Drones"
              value={formData.projectTitle}
              onChange={(e) => setFormData({ ...formData, projectTitle: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.25rem' }}>
            <div className="form-group">
              <label className="form-label" htmlFor="team-lead-input">
                Team Lead Identifier *
              </label>
              <input
                id="team-lead-input"
                className="form-control"
                type="text"
                placeholder="e.g. alex_rivera"
                value={formData.teamLead}
                onChange={(e) => setFormData({ ...formData, teamLead: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="track-select">
                Hackathon Track *
              </label>
              <select
                id="track-select"
                className="form-control"
                value={formData.track}
                onChange={(e) => setFormData({ ...formData, track: e.target.value })}
                required
              >
                {tracks.map((t) => (
                  <option key={t.value} value={t.value}>
                    {t.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="project-description-input">
              Project Abstract / Description *
            </label>
            <textarea
              id="project-description-input"
              className="form-control"
              rows="4"
              placeholder="Outline your problem statement, technical stack, architecture, and expected impact..."
              value={formData.projectDescription}
              onChange={(e) => setFormData({ ...formData, projectDescription: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '2rem' }}>
            <Link to="/teams" className="btn btn-secondary" id="cancel-team-btn">
              Cancel
            </Link>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              id="submit-team-btn"
            >
              <Save size={16} />
              <span>{loading ? 'Saving...' : isEdit ? 'Update Team' : 'Complete Registration'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TeamForm;
