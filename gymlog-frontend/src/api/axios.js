import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor — attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('gymlog_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor — handle 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry && originalRequest.url !== '/auth/refresh' && originalRequest.url !== '/auth/login') {
      originalRequest._retry = true;
      try {
        const token = localStorage.getItem('gymlog_token');
        const res = await axios.post(`${API_BASE_URL}/auth/refresh`, { token });
        const newToken = res.data.token;
        localStorage.setItem('gymlog_token', newToken);
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('gymlog_token');
        localStorage.removeItem('gymlog_user');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

// ============ AUTH API ============
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  changePassword: (data) => api.put('/auth/change-password', data),
};

// ============ USER API ============
export const userAPI = {
  getProfile: () => api.get('/users/me'),
  updateProfile: (data) => api.put('/users/me', data),
};

// ============ WORKOUT API ============
export const workoutAPI = {
  create: (data) => api.post('/workouts', data),
  getById: (id) => api.get(`/workouts/${id}`),
  getHistory: (page = 0, size = 10) => api.get(`/workouts/history?page=${page}&size=${size}`),
  getByDate: (date) => api.get(`/workouts/date/${date}`),
  update: (id, data) => api.put(`/workouts/${id}`, data),
  delete: (id) => api.delete(`/workouts/${id}`),
};

// ============ EXERCISE API ============
export const exerciseAPI = {
  getAll: (page = 0, size = 50) => api.get(`/exercises?page=${page}&size=${size}`),
  getById: (id) => api.get(`/exercises/${id}`),
  search: (query, page = 0) => api.get(`/exercises/search?query=${query}&page=${page}`),
  getByCategory: (catId, page = 0) => api.get(`/exercises/category/${catId}?page=${page}`),
  getCategories: () => api.get('/exercise-categories'),
  create: (data) => api.post('/exercises', data),
  update: (id, data) => api.put(`/exercises/${id}`, data),
  delete: (id) => api.delete(`/exercises/${id}`),
};

// ============ NUTRITION API ============
export const nutritionAPI = {
  create: (data) => api.post('/nutrition', data),
  getByDate: (date) => api.get(`/nutrition/date/${date}`),
  getDailySummary: (date) => api.get(`/nutrition/daily-summary/${date}`),
  getHistory: (page = 0, size = 20) => api.get(`/nutrition/history?page=${page}&size=${size}`),
  update: (id, data) => api.put(`/nutrition/${id}`, data),
  delete: (id) => api.delete(`/nutrition/${id}`),
};

// ============ BODY MEASUREMENTS API ============
export const bodyMeasurementAPI = {
  create: (data) => api.post('/body-measurements', data),
  getHistory: (page = 0, size = 20) => api.get(`/body-measurements/history?page=${page}&size=${size}`),
  getAll: () => api.get('/body-measurements/all'),
  delete: (id) => api.delete(`/body-measurements/${id}`),
};

// ============ GOALS API ============
export const goalAPI = {
  create: (data) => api.post('/goals', data),
  getAll: (page = 0, size = 10) => api.get(`/goals?page=${page}&size=${size}`),
  getActive: () => api.get('/goals/active'),
  update: (id, data) => api.put(`/goals/${id}`, data),
  updateProgress: (id, value) => api.put(`/goals/${id}/progress?value=${value}`),
  delete: (id) => api.delete(`/goals/${id}`),
};

// ============ DASHBOARD API ============
export const dashboardAPI = {
  get: () => api.get('/dashboard'),
};

// ============ ANALYTICS API ============
export const analyticsAPI = {
  getWeightProgression: () => api.get('/analytics/weight'),
  getStrengthProgression: (exerciseId) => api.get(`/analytics/strength/${exerciseId}`),
  getVolumeTrends: (days = 30) => api.get(`/analytics/volume?days=${days}`),
  getCalorieTrends: (days = 30) => api.get(`/analytics/calories?days=${days}`),
};

// ============ ADMIN API ============
export const adminAPI = {
  getStats: () => api.get('/admin/stats'),
  getUsers: (page = 0, size = 20) => api.get(`/admin/users?page=${page}&size=${size}`),
  toggleUserStatus: (id) => api.put(`/admin/users/${id}/toggle-status`),
};

export default api;
