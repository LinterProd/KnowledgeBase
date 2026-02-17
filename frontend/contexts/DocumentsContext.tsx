"use client";

import React, { createContext, useState, useEffect, useContext, ReactNode } from 'react';
import { DocumentResponseDto, DocumentStatus } from '@/types/types';
import { DocumentService } from '@/services/DocumentService';
import { useAuth } from '@/contexts/AuthContext';

interface DocumentsContextType {
    documents: DocumentResponseDto[];
    loading: boolean;
    error: string | null;
    fetchDocuments: (status?: DocumentStatus, page?: number, size?: number, sort?: string) => Promise<void>;
    fetchDocumentById: (id: string) => Promise<DocumentResponseDto>;
    createNewDocument: (file: File) => Promise<DocumentResponseDto>;
    updateExistingDocument: (id: string, file?: File) => Promise<DocumentResponseDto>;
    deleteExistingDocument: (id: string) => Promise<void>;
    getExistingDocumentUrl: (id: string) => Promise<string>;
    downloadExistingDocument: (id: string) => Promise<Blob>;
}

const DocumentsContext = createContext<DocumentsContextType | undefined>(undefined);

export const DocumentsProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const { isAuthenticated } = useAuth();
    const [documents, setDocuments] = useState<DocumentResponseDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchDocuments = async (
        status: DocumentStatus = DocumentStatus.PUBLISHED
    ) => {
        setLoading(true);
        setError(null);
        try {
            const data = await DocumentService.getDocumentsByStatus(status);
            setDocuments(data);
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setLoading(false);
        }
    };

    const fetchDocumentById = async (id: string) => {
        setLoading(true);
        setError(null);
        try {
            return await DocumentService.getDocumentById(id);
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const createNewDocument = async (file: File) => {
        if (!isAuthenticated) throw new Error('Not authenticated');
        setLoading(true);
        setError(null);
        try {
            const newDoc = await DocumentService.createDocument(file);
            setDocuments([newDoc, ...documents]);
            return newDoc;
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const updateExistingDocument = async (id: string, file?: File) => {
        if (!isAuthenticated) throw new Error('Not authenticated');
        setLoading(true);
        setError(null);
        try {
            const updatedDoc = await DocumentService.updateDocument(id, file);
            setDocuments(documents.map((doc) => (doc.id === id ? updatedDoc : doc)));
            return updatedDoc;
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const deleteExistingDocument = async (id: string) => {
        if (!isAuthenticated) throw new Error('Not authenticated');
        setLoading(true);
        setError(null);
        try {
            await DocumentService.deleteDocument(id);
            setDocuments(documents.filter((doc) => doc.id !== id));
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const getExistingDocumentUrl = async (id: string) => {
        setLoading(true);
        setError(null);
        try {
            return await DocumentService.getDocumentUrl(id);
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const downloadExistingDocument = async (id: string) => {
        setLoading(true);
        setError(null);
        try {
            return await DocumentService.downloadDocument(id);
        } catch (err) {
            setError((err as Error).message);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    // Авто-загрузка документов при монтировании, если аутентифицирован
    useEffect(() => {
        if (isAuthenticated) {
            fetchDocuments();
        }
    }, [isAuthenticated]);

    return (
        <DocumentsContext.Provider
            value={{
                documents,
                loading,
                error,
                fetchDocuments,
                fetchDocumentById,
                createNewDocument,
                updateExistingDocument,
                deleteExistingDocument,
                getExistingDocumentUrl,
                downloadExistingDocument,
            }}
        >
            {children}
        </DocumentsContext.Provider>
    );
};

export const useDocuments = (): DocumentsContextType => {
    const context = useContext(DocumentsContext);
    if (!context) {
        throw new Error('useDocuments must be used within a DocumentsProvider');
    }
    return context;
};