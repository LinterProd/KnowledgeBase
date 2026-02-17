import React from 'react';
import {FileIcon} from "@/components/icons";

interface FileButtonProps {
    file: {
        id: string;
        name: string;
    };
    onClick: (file: { id: string; name: string }) => void;
}

export const FileButton = ({ file, onClick }: FileButtonProps) => {
    const getFileType = (filename: string): string => {
        const ext = filename.split('.').pop()?.toLowerCase();
        const typeMap: { [key: string]: string } = {
            'pdf': 'pdf',
            'doc': 'document', 'docx': 'document', 'txt': 'document', 'rtf': 'document',
            'xls': 'spreadsheet', 'xlsx': 'spreadsheet', 'csv': 'spreadsheet',
            'jpg': 'image', 'jpeg': 'image', 'png': 'image', 'gif': 'image',
            'bmp': 'image', 'svg': 'image', 'webp': 'image',
            'zip': 'archive', 'rar': 'archive', '7z': 'archive', 'tar': 'archive',
            'mp3': 'audio', 'wav': 'audio', 'ogg': 'audio', 'flac': 'audio',
            'mp4': 'video', 'avi': 'video', 'mkv': 'video', 'mov': 'video'
        };
        return typeMap[ext || ''] || 'file';
    };

    return (
        <button
            onClick={() => onClick(file)}
            className="flex items-center w-full py-2 px-3 text-left text-gray-300 hover:bg-gray-700 rounded-md transition-colors group"
        >
            <FileIcon
                type={getFileType(file.name)}
                className="w-4 h-4 mr-3 flex-shrink-0 text-blue-400"
            />
            <span className="truncate text-sm group-hover:text-white transition-colors">
                {file.name}
            </span>
        </button>
    );
};