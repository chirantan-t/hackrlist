import apiClient from './apiClient';

export const teamService = {
  getTeams: async (params = {}) => {
    const response = await apiClient.get('/api/teams', { params });
    return response.data;
  },

  searchTeams: async (query, params = {}) => {
    const response = await apiClient.get('/api/teams/search', {
      params: { q: query, ...params }
    });
    return response.data;
  },

  getTeamById: async (id) => {
    const response = await apiClient.get(`/api/teams/${id}`);
    return response.data;
  },

  createTeam: async (teamData) => {
    const response = await apiClient.post('/api/teams', teamData);
    return response.data;
  },

  updateTeam: async (id, teamData) => {
    const response = await apiClient.put(`/api/teams/${id}`, teamData);
    return response.data;
  },

  deleteTeam: async (id) => {
    const response = await apiClient.delete(`/api/teams/${id}`);
    return response.data;
  },

  updateStatus: async (id, status, remarks = '') => {
    const response = await apiClient.patch(`/api/teams/${id}/status`, { status, remarks });
    return response.data;
  }
};
