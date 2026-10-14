import React, { useState, useEffect } from 'react';
import { UserPlus, X, AlertCircle } from 'lucide-react';

const memberRoles = [
  'FRONTEND',
  'BACKEND',
  'FULLSTACK',
  'UI_UX',
  'DATA_SCIENTIST',
  'DEVOPS'
];

export const MemberModal = ({ isOpen, member, onSave, onClose }) => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    college: '',
    department: '',
    teamRole: 'FULLSTACK'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (member) {
      setFormData({
        fullName: member.fullName || '',
        email: member.email || '',
        college: member.college || '',
        department: member.department || '',
        teamRole: member.teamRole || 'FULLSTACK'
      });
    } else {
      setFormData({
        fullName: '',
        email: '',
        college: '',
        department: '',
        teamRole: 'FULLSTACK'
      });
    }
    setError('');
  }, [member, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await onSave(formData);
      onClose();
    } catch (err) {
      setError(err.extractedMessage || 'Failed to save member record.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" id="member-modal">
      <div className="modal-content">
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <UserPlus color="var(--accent-primary)" size={22} />
            <h3 style={{ fontSize: '1.15rem' }}>
              {member ? 'Edit Team Member' : 'Enroll New Member'}
            </h3>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {error && (
              <div className="alert alert-danger" id="member-error">
                <AlertCircle size={18} />
                <span>{error}</span>
              </div>
            )}

            <div className="form-group">
              <label className="form-label" htmlFor="member-fullname">Full Name *</label>
              <input
                id="member-fullname"
                className="form-control"
                type="text"
                placeholder="e.g. Sarah Connor"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="member-email">Email Address *</label>
              <input
                id="member-email"
                className="form-control"
                type="email"
                placeholder="e.g. sarah@institution.edu"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label" htmlFor="member-college">College / Institution *</label>
                <input
                  id="member-college"
                  className="form-control"
                  type="text"
                  placeholder="e.g. MIT"
                  value={formData.college}
                  onChange={(e) => setFormData({ ...formData, college: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="member-department">Department *</label>
                <input
                  id="member-department"
                  className="form-control"
                  type="text"
                  placeholder="e.g. Computer Science"
                  value={formData.department}
                  onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="member-role">Team Role *</label>
              <select
                id="member-role"
                className="form-control"
                value={formData.teamRole}
                onChange={(e) => setFormData({ ...formData, teamRole: e.target.value })}
                required
              >
                {memberRoles.map((r) => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              id="member-cancel-btn"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              id="member-submit-btn"
            >
              {loading ? 'Saving...' : member ? 'Update Member' : 'Enroll Member'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default MemberModal;
