"use client";

import SidebarHeader from './SidebarHeader';
import NavItem from './NavItem';
import { FileAccordion } from './FileAccordion';
import { ChatIcon } from '@/components/icons';
import {useDocuments} from "@/contexts/DocumentsContext";

const Sidebar = () => {
    const { documents } = useDocuments();

    const files = documents.map(doc => ({
        id: doc.id,
        name: doc.title,
        type: doc.title.split('.').pop()?.toLowerCase() || 'file'
    }));

    return (
        <aside className="fixed left-0 top-0 h-screen w-64 bg-gray-800 border-r border-gray-700 rounded-r-lg overflow-y-auto">
            <SidebarHeader title="My App" subtitle="Dashboard" />

            <nav className="flex flex-col space-y-2 px-4">
                <NavItem icon="🏠" label="Главный экран" href="/" />
                <NavItem icon="📁" label="Все документы" href="/documents" />
                <NavItem icon="👤" label="Пользователи" href="/users" />
                <NavItem icon={<ChatIcon />} label="Chat with AI" />
                <NavItem icon="🔑" label="Авторизация" href="/auth" />
            </nav>

            <FileAccordion files={files} />
        </aside>
    );
};

export default Sidebar;