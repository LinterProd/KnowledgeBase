import axios, { AxiosInstance } from 'axios';
import { AuthService } from '@/services/AuthService';
import {getAccessToken} from "@/services/StorageService";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

const api: AxiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use(
    (config) => {
        if (typeof window !== 'undefined') {
            const accessToken = getAccessToken();
            if (accessToken) {
                config.headers.set('Authorization', `Bearer ${accessToken}`);
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config as typeof error.config & { _retry?: boolean };

        if (originalRequest.url?.includes('/auth/refresh')) {
            return Promise.reject(error);
        }

        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const { accessToken } = await AuthService.refresh();
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                AuthService.logout();
                return Promise.reject(refreshError);
            }
        }
        console.error('API Error:', error.response?.status || 500, error.message);
        return Promise.reject(error);
    }
);

export default api;