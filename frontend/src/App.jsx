import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { TeamList } from './pages/TeamList';
import { TeamDetails } from './pages/TeamDetails';
import { TeamForm } from './pages/TeamForm';

const ProtectedLayout = ({ children, title }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: 'var(--bg-primary)',
        color: 'var(--text-secondary)'
      }}>
        Loading Session Context...
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <Navbar title={title} />
        <main>{children}</main>
      </div>
    </div>
  );
};

export const App = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route
            path="/"
            element={
              <ProtectedLayout title="Executive Overview & Analytics">
                <Dashboard />
              </ProtectedLayout>
            }
          />

          <Route
            path="/teams"
            element={
              <ProtectedLayout title="Hackathon Team Directory">
                <TeamList />
              </ProtectedLayout>
            }
          />

          <Route
            path="/teams/new"
            element={
              <ProtectedLayout title="Enroll New Team">
                <TeamForm />
              </ProtectedLayout>
            }
          />

          <Route
            path="/teams/:id"
            element={
              <ProtectedLayout title="Team Profile & Roster">
                <TeamDetails />
              </ProtectedLayout>
            }
          />

          <Route
            path="/teams/:id/edit"
            element={
              <ProtectedLayout title="Modify Team Information">
                <TeamForm />
              </ProtectedLayout>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
