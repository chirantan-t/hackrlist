import React, { useState } from 'react';
import { GitBranch, X, AlertCircle } from 'lucide-react';
import StatusBadge from './StatusBadge';

const allowedTransitions = {
  DRAFT: ['REGISTERED'],
  REGISTERED: ['APPROVED', 'REJECTED'],
  APPROVED: ['IN_PROGRESS'],
  IN_PROGRESS: ['SUBMITTED'],
  SUBMITTED: ['COMPLETED', 'REJECTED'],
  REJECTED: ['REGISTERED'],
  COMPLETED: []
};

export const StatusTransitionModal = ({ isOpen, currentStatus, teamName, onTransition, onClose }) => {
  const [selectedStatus, setSelectedStatus] = useState('');
  const [remarks, setRemarks] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const validTargets = allowedTransitions[currentStatus] || [];

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedStatus) {
      setError('Please select a target status');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onTransition(selectedStatus, remarks);
      onClose();
    } catch (err) {
      setError(err.extractedMessage || 'Status transition failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" id="status-transition-modal">
      <div className="modal-content">
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <GitBranch color="var(--accent-primary)" size={22} />
            <h3 style={{ fontSize: '1.15rem' }}>Transition Status: {teamName}</h3>
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
              <div className="alert alert-danger" id="status-transition-error">
                <AlertCircle size={18} />
                <span>{error}</span>
              </div>
            )}

            <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Current Status:</span>
              <StatusBadge status={currentStatus} />
            </div>

            {validTargets.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>
                This team has reached the final terminal workflow state ({currentStatus}). No further transitions are available.
              </p>
            ) : (
              <>
                <div className="form-group">
                  <label className="form-label" htmlFor="status-select">
                    Next Permitted Status
                  </label>
                  <select
                    id="status-select"
                    className="form-control"
                    value={selectedStatus}
                    onChange={(e) => setSelectedStatus(e.target.value)}
                    required
                  >
                    <option value="">-- Choose Target Status --</option>
                    {validTargets.map((st) => (
                      <option key={st} value={st}>
                        {st}
                      </option>
                    ))}
                  </select>
                  <small style={{ color: 'var(--text-muted)', display: 'block', marginTop: '0.35rem' }}>
                    Workflow rule: {currentStatus} &rarr; {validTargets.join(' | ')}
                  </small>
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="status-remarks">
                    Audit Remarks / Notes
                  </label>
                  <textarea
                    id="status-remarks"
                    className="form-control"
                    rows="3"
                    placeholder="Enter reason or evaluation notes..."
                    value={remarks}
                    onChange={(e) => setRemarks(e.target.value)}
                  />
                </div>
              </>
            )}
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              id="status-cancel-btn"
            >
              Cancel
            </button>
            {validTargets.length > 0 && (
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading || !selectedStatus}
                id="status-submit-btn"
              >
                {loading ? 'Updating...' : 'Apply Transition'}
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default StatusTransitionModal;
