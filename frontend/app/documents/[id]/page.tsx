"use client";

import { useParams } from 'next/navigation';
import { useDocuments } from '@/contexts/DocumentsContext';
import { useEffect, useState } from 'react';
import { DocumentResponseDto } from '@/types/types';
import Sidebar from "@/components/sidebar/Sidebar";

const DocumentPage = () => {
    const params = useParams();
    const documentId = params.id as string;

    const { fetchDocumentById, getExistingDocumentUrl, downloadExistingDocument } = useDocuments();
    const [document, setDocument] = useState<DocumentResponseDto | null>(null);
    const [fileUrl, setFileUrl] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const loadDocument = async () => {
            try {
                const doc = await fetchDocumentById(documentId);
                console.log(doc);
                setDocument(doc);
                const url = await getExistingDocumentUrl(documentId);
                setFileUrl(url);
            } catch (error) {
                console.error('Ошибка загрузки документа:', error);
                setError('Не удалось загрузить документ');
            } finally {
                setLoading(false);
            }
        };

        if (documentId) {
            loadDocument();
        }
    }, [documentId]);

    const getFileType = (filename: string): string => {
        const ext = filename.split('.').pop()?.toLowerCase() || '';
        if (['pdf'].includes(ext)) return 'pdf';
        if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp', 'svg'].includes(ext)) return 'image';
        if (['mp4', 'avi', 'mov', 'mkv', 'webm'].includes(ext)) return 'video';
        if (['mp3', 'wav', 'ogg', 'flac'].includes(ext)) return 'audio';
        if (['txt', 'md', 'json', 'xml', 'csv', 'log', 'js', 'ts', 'html', 'css'].includes(ext)) return 'text';
        if (['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(ext)) return 'office';
        return 'other';
    };

    const renderFullDocument = () => {
        if (!document || !fileUrl) return null;

        const fileType = getFileType(document.title);

        switch (fileType) {
            case 'pdf':
                return (
                    <div className="w-full h-[calc(100vh-80px)]"> {/* Учитываем высоту заголовка */}
                        <iframe
                            src={fileUrl}
                            className="w-full h-full border-0"
                            title={document.title}
                        />
                    </div>
                );

            case 'image':
                return (
                    <div className="flex justify-center items-center bg-black min-h-[calc(100vh-80px)] p-4">
                        <img
                            src={fileUrl}
                            alt={document.title}
                            className="max-w-full max-h-full object-contain"
                            onClick={() => window.open(fileUrl, '_blank')}
                        />
                    </div>
                );

            case 'text':
                return <TextViewer fileUrl={fileUrl} />;

            case 'video':
                return (
                    <div className="flex justify-center items-center bg-black min-h-[calc(100vh-80px)] p-4">
                        <video
                            controls
                            autoPlay
                            className="max-w-full max-h-full"
                        >
                            <source src={fileUrl} type={`video/${document.title.split('.').pop()}`} />
                            Ваш браузер не поддерживает видео.
                        </video>
                    </div>
                );

            case 'audio':
                return (
                    <div className="flex justify-center items-center min-h-48 p-8">
                        <audio
                            controls
                            autoPlay
                            className="w-full max-w-2xl"
                        >
                            <source src={fileUrl} type={`audio/${document.title.split('.').pop()}`} />
                            Ваш браузер не поддерживает аудио.
                        </audio>
                    </div>
                );

            case 'office':
            case 'other':
            default:
                return (
                    <div className="flex flex-col items-center justify-center min-h-64 p-8">
                        <div className="text-6xl mb-4">📄</div>
                        <p className="text-gray-400 mb-4 text-center">
                            Просмотр {document.title} недоступен в браузере
                        </p>
                        <button
                            onClick={() => downloadExistingDocument(documentId)}
                            className="bg-blue-600 hover:bg-blue-700 px-6 py-3 rounded-lg"
                        >
                            Скачать файл
                        </button>
                    </div>
                );
        }
    };

    if (loading) return (
        <div className="min-h-screen bg-gray-900 flex">
            <div className="ml-64 flex-1 p-8">Загрузка документа...</div>
        </div>
    );

    if (error) return (
        <div className="min-h-screen bg-gray-900 flex">
            <div className="ml-64 flex-1 p-8 text-red-400">{error}</div>
        </div>
    );

    if (!document) return (
        <div className="min-h-screen bg-gray-900 flex">
            <div className="ml-64 flex-1 p-8">Документ не найден</div>
        </div>
    );

    return (
        <div className="min-h-screen bg-gray-900 flex">
            {/* Основной контент с отступом */}
            <div className="ml-64 flex-1 flex flex-col">
                {/* Заголовок */}
                <div className="bg-gray-800 p-4 sticky top-0 z-10 shadow-lg">
                    <div className="flex justify-between items-center">
                        <div>
                            <h1 className="text-lg font-bold truncate max-w-md">{document.title}</h1>
                            <p className="text-gray-400 text-sm">
                                {getFileType(document.title).toUpperCase()} •
                                {document.createdAt ? new Date(document.createdAt).toLocaleDateString() : 'Дата не указана'}
                            </p>
                        </div>
                        <button
                            onClick={() => downloadExistingDocument(documentId)}
                            className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded text-sm"
                        >
                            Скачать
                        </button>
                    </div>
                </div>

                {/* Контент документа */}
                <div className="flex-1">
                    {renderFullDocument()}
                </div>
            </div>
        </div>
    );
};

const TextViewer = ({ fileUrl }: { fileUrl: string }) => {
    const [content, setContent] = useState<string>('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadText = async () => {
            try {
                const response = await fetch(fileUrl);
                const text = await response.text();
                setContent(text);
            } catch (error) {
                console.error('Ошибка загрузки текста:', error);
                setContent('Не удалось загрузить содержимое файла');
            } finally {
                setLoading(false);
            }
        };

        loadText();
    }, [fileUrl]);

    if (loading) return <div className="p-8 text-center">Загрузка текста...</div>;

    return (
        <div className="p-8">
            <pre className="bg-gray-800 rounded-lg p-6 whitespace-pre-wrap font-mono text-sm text-gray-100 max-h-[calc(100vh-160px)] overflow-auto">
                {content}
            </pre>
        </div>
    );
};

export default DocumentPage;