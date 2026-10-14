import React from 'react';

const statusConfig = {
  DRAFT: { label: 'Draft', class: 'badge-draft' },
  REGISTERED: { label: 'Registered', class: 'badge-registered' },
  APPROVED: { label: 'Approved', class: 'badge-approved' },
  IN_PROGRESS: { label: 'In Progress', class: 'badge-in_progress' },
  SUBMITTED: { label: 'Submitted', class: 'badge-submitted' },
  COMPLETED: { label: 'Completed', class: 'badge-completed' },
  REJECTED: { label: 'Rejected', class: 'badge-rejected' },
};

export const StatusBadge = ({ status }) => {
  const config = statusConfig[status] || { label: status, class: 'badge-draft' };

  return (
    <span className={`badge ${config.class}`} data-testid={`status-badge-${status}`}>
      ● {config.label}
    </span>
  );
};

export default StatusBadge;
