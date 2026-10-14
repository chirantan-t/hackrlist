import React from 'react';
import { useAuth } from '../context/AuthContext';
import { ShieldCheck, Activity } from 'lucide-react';

export const Navbar = ({ title = 'Hackathon Operations Console' }) => {
  const { role } = useAuth();

  return (
    <header className="top-navbar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <h1 style={{ fontSize: '1.25rem', fontWeight: '700' }}>{title}</h1>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          padding: '0.35rem 0.8rem',
          background: 'rgba(16, 185, 129, 0.1)',
          border: '1px solid rgba(16, 185, 129, 0.25)',
          borderRadius: 'var(--radius-full)',
          fontSize: '0.8rem',
          color: 'var(--accent-emerald)',
          fontWeight: '500'
        }}>
          <Activity size={14} className="pulse-icon" />
          <span>System Healthy</span>
        </div>

        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.4rem',
          fontSize: '0.85rem',
          color: 'var(--text-secondary)'
        }}>
          <ShieldCheck size={16} color="var(--accent-primary)" />
          <span>Role: <strong style={{ color: 'var(--text-primary)' }}>{role}</strong></span>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
