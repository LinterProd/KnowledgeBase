"use client";

import api from '@/lib/api/client';
import { DocumentResponseDto, DocumentStatus } from '@/types/types';
import { AxiosError } from 'axios';

export class DocumentService {
    private static handleError(error: unknown, action: string): never {
        const axiosError = error as AxiosError;
        console.error(`${action} error:`, axiosError.response?.status || 500, axiosError.message);
        throw error;
    }

    static async createDocument(file: File): Promise<DocumentResponseDto> {
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await api.post<DocumentResponseDto>('/api/documents', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            return response.data;
        } catch (error) {
            this.handleError(error, 'Create document');
        }
    }

    static async getDocumentsByStatus(status?: DocumentStatus): Promise<DocumentResponseDto[]> {
        try {
            const response = await api.get<DocumentResponseDto[]>('/api/documents', {
                params: status ? { status } : undefined,
            });
            return response.data;
        } catch (error) {
            this.handleError(error, 'Get documents by status');
        }
    }

    static async getDocumentById(id: string): Promise<DocumentResponseDto> {
        try {
            const response = await api.get<DocumentResponseDto>(`/api/documents/${id}`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Get document by ID');
        }
    }

    static async updateDocument(id: string, file?: File): Promise<DocumentResponseDto> {
        try {
            const formData = new FormData();
            if (file) {
                formData.append('file', file);
            }

            const response = await api.put<DocumentResponseDto>(`/api/documents/${id}`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            return response.data;
        } catch (error) {
            this.handleError(error, 'Update document');
        }
    }

    static async downloadDocument(id: string): Promise<Blob> {
        try {
            const response = await api.get(`/api/documents/${id}/download`, {
                responseType: 'blob',
            });
            return response.data;
        } catch (error) {
            this.handleError(error, 'Download document');
        }
    }

    static async deleteDocument(id: string): Promise<void> {
        try {
            await api.post(`/api/documents/${id}/archive`);
        } catch (error) {
            this.handleError(error, 'Archive document');
        }
    }

    static async getDocumentUrl(id: string): Promise<string> {
        try {
            const response = await api.get<string>(`/api/documents/${id}/url`);
            return response.data;
        } catch (error) {
            this.handleError(error, 'Get document URL');
        }
    }
}