import React, { createContext, useContext, useState, useEffect } from 'react';
import apiClient from '../services/apiClient';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('htm_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Validate current session against backend
    apiClient.get('/api/auth/me')
      .then(res => {
        setUser(res.data);
        localStorage.setItem('htm_user', JSON.stringify(res.data));
      })
      .catch(() => {
        // Not authenticated
        setUser(null);
        localStorage.removeItem('htm_user');
      })
      .finally(() => setLoading(false));
  }, []);

  const login = async (username, password) => {
    const res = await apiClient.post('/api/auth/login', { username, password });
    const userData = res.data;
    setUser(userData);
    localStorage.setItem('htm_user', JSON.stringify(userData));
    return userData;
  };

  const logout = async () => {
    try {
      await apiClient.post('/api/auth/logout');
    } catch (e) {
      // Ignore network errors on logout
    } finally {
      setUser(null);
      localStorage.removeItem('htm_user');
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
      role: user?.role,
      isAuthenticated: !!user,
      isAdmin: user?.role === 'ADMIN',
      isOrganizer: user?.role === 'ORGANIZER',
      isLead: user?.role === 'TEAM_LEAD',
      login,
      logout,
      loading
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
