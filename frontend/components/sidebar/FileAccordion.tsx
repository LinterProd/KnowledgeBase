"use client";

import { useState } from 'react';
import {FileIcon} from "@/components/icons";
import { useRouter } from 'next/navigation';
import {FileButton} from "@/components/sidebar/FileButton";

interface FileItem {
    id: string;
    name: string;
}

interface FileAccordionProps {
    files: FileItem[];
}

export const FileAccordion = ({ files }: FileAccordionProps) => {
    const router = useRouter();
    const [isOpen, setIsOpen] = useState(false);

    const handleFileClick = (file: FileItem) => {
        router.push(`/documents/${file.id}`);
    };

    return (
        <div className="absolute bottom-0 w-full p-4 border-t border-gray-700">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center justify-between w-full py-2 px-4 text-left text-white hover:bg-gray-700 rounded-md transition-colors"
            >
                <span className="flex items-center">
                    <FileIcon type="folder" className="w-4 h-4 mr-2" />
                    Files ({files.length})
                </span>
                <span>{isOpen ? '▲' : '▼'}</span>
            </button>

            {isOpen && (
                <div className="mt-2 space-y-1 max-h-80 overflow-y-auto">
                    {files.map((file) => (
                        <FileButton
                            key={file.id}
                            file={file}
                            onClick={handleFileClick}
                        />
                    ))}
                </div>
            )}
        </div>
    );
};