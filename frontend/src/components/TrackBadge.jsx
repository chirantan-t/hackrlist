import React from 'react';

const trackLabels = {
  AI_ML: 'AI & Machine Learning',
  WEB3_BLOCKCHAIN: 'Web3 & Blockchain',
  FINTECH: 'FinTech & DeFi',
  HEALTHTECH: 'HealthTech & Bio',
  OPEN_INNOVATION: 'Open Innovation',
  CYBERSECURITY: 'Cybersecurity'
};

export const TrackBadge = ({ track }) => {
  const label = trackLabels[track] || track;

  return (
    <span style={{
      display: 'inline-flex',
      alignItems: 'center',
      padding: '0.2rem 0.6rem',
      borderRadius: 'var(--radius-sm)',
      fontSize: '0.75rem',
      fontWeight: '600',
      backgroundColor: 'rgba(99, 102, 241, 0.12)',
      color: '#a5b4fc',
      border: '1px solid rgba(99, 102, 241, 0.25)'
    }}>
      {label}
    </span>
  );
};

export default TrackBadge;
