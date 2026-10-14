import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true // Supports same-origin and cross-origin secure session cookies
});

// Interceptor for centralized API error extraction
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    let message = 'An unexpected server error occurred.';
    if (error.response?.data?.message) {
      message = error.response.data.message;
    } else if (error.response?.statusText) {
      message = `${error.response.status}: ${error.response.statusText}`;
    } else if (error.message) {
      message = error.message;
    }
    error.extractedMessage = message;
    return Promise.reject(error);
  }
);

export default apiClient;
