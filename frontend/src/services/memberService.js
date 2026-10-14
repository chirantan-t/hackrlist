import apiClient from './apiClient';

export const memberService = {
  getMembers: async (teamId) => {
    const response = await apiClient.get(`/api/teams/${teamId}/members`);
    return response.data;
  },

  addMember: async (teamId, memberData) => {
    const response = await apiClient.post(`/api/teams/${teamId}/members`, memberData);
    return response.data;
  },

  updateMember: async (teamId, memberId, memberData) => {
    const response = await apiClient.put(`/api/teams/${teamId}/members/${memberId}`, memberData);
    return response.data;
  },

  deleteMember: async (teamId, memberId) => {
    const response = await apiClient.delete(`/api/teams/${teamId}/members/${memberId}`);
    return response.data;
  }
};
