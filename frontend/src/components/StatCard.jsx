import React from 'react';

export const StatCard = ({ label, value, icon: Icon, color = 'var(--accent-primary)', id }) => {
  return (
    <div className="card stat-card" id={id}>
      <div
        className="stat-icon-wrapper"
        style={{
          backgroundColor: `rgba(99, 102, 241, 0.12)`,
          color: color,
          border: `1px solid rgba(255, 255, 255, 0.08)`
        }}
      >
        {Icon && <Icon size={24} />}
      </div>
      <div className="stat-content">
        <span className="stat-label">{label}</span>
        <span className="stat-value">{value}</span>
      </div>
    </div>
  );
};

export default StatCard;
