"use client";

import api from '@/lib/api/client';
import { UserCreateDto, UserResponseDto, UserUpdateDto } from '@/types/types';
import { AxiosError } from 'axios';

export class UserService {
    private static handleError(error: unknown, action: string): never {
        const axiosError = error as AxiosError;
        console.error(`${action} error:`, axiosError.response?.status || 500, axiosError.message);
        throw error;
    }

    static async createUser(dto: UserCreateDto): Promise<UserResponseDto> {
        try {
            const response = await api.post<UserResponseDto>('/api/users', dto);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Create user');
        }
    }

    static async getAllUsers(): Promise<UserResponseDto[]> {
        try {
            const response = await api.get<UserResponseDto[]>('/api/users');
            return response.data;
        } catch (error) {
            this.handleError(error, 'Get all users');
        }
    }

    static async getUserById(id: number): Promise<UserResponseDto> {
        try {
            const response = await api.get<UserResponseDto>(`/api/users/${id}`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Get user by ID');
        }
    }

    static async updateUser(id: number, dto: UserUpdateDto): Promise<UserResponseDto> {
        try {
            const response = await api.put<UserResponseDto>(`/api/users/${id}`, dto);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Update user');
        }
    }

    static async suspendUser(id: number): Promise<void> {
        try {
            await api.post(`/api/users/${id}/suspend`);
        } catch (error) {
            this.handleError(error, 'Suspend user');
        }
    }

    static async activateUser(id: number): Promise<void> {
        try {
            await api.post(`/api/users/${id}/activate`);
        } catch (error) {
            this.handleError(error, 'Activate user');
        }
    }
}