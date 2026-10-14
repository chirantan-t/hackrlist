import apiClient from './apiClient';

export const dashboardService = {
  getSummary: async () => {
    const response = await apiClient.get('/api/dashboard/summary');
    return response.data;
  },

  getStatusDistribution: async () => {
    const response = await apiClient.get('/api/dashboard/status-distribution');
    return response.data;
  },

  getTrackDistribution: async () => {
    const response = await apiClient.get('/api/dashboard/track-distribution');
    return response.data;
  }
};
