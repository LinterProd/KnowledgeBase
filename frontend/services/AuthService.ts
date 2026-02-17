"use client";

import api from '@/lib/api/client';
import { AuthRequest, AuthResponse, UserCreateDto, UserResponseDto } from '@/types/types';
import { AxiosError } from 'axios';
import {
    clearAllAuthData,
    getRefreshToken,
    setAuthData
} from "@/services/StorageService";

export class AuthService {
    static async register(dto: UserCreateDto): Promise<UserResponseDto> {
        try {
            const response = await api.post('/api/auth/register', dto);
            const { accessToken, refreshToken, user } = response.data;
            setAuthData(accessToken, refreshToken, user);
            return response.data;
        } catch (error) {
            const axiosError = error as AxiosError;
            console.error('Register error:', axiosError.response?.status || 500, axiosError.message);
            throw error;
        }
    }

    static async login(request: AuthRequest): Promise<AuthResponse> {
        try {
            const response = await api.post<AuthResponse>('/api/auth/login', request);
            const { accessToken, refreshToken, user } = response.data;
            setAuthData(accessToken, refreshToken, user);
            return response.data;
        } catch (error) {
            const axiosError = error as AxiosError;
            console.error('Login error:', axiosError.response?.status || 500, axiosError.message);
            throw error;
        }
    }

    static async refresh(): Promise<AuthResponse> {
        const refreshToken = getRefreshToken();
        if (!refreshToken) throw new Error('No refresh token for refresh');

        try {
            console.log(refreshToken);
            const response = await api.post<AuthResponse>('/api/auth/refresh', { refreshToken });
            const { accessToken, refreshToken: newRefreshToken, user } = response.data;
            setAuthData(accessToken, newRefreshToken, user);
            return response.data;
        } catch (error) {
            const axiosError = error as AxiosError;
            console.error('Refresh error:', axiosError.response?.status || 500, axiosError.message);
            throw error;
        }
    }

    static logout(): void {
        clearAllAuthData();
    }
}