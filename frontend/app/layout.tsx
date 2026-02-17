import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import {AuthProvider} from "@/contexts/AuthContext";
import {DocumentsProvider} from "@/contexts/DocumentsContext";
import React from "react";
import Sidebar from "@/components/sidebar/Sidebar";

const inter = Inter({
    variable: "--font-sans",
    subsets: ["latin", "cyrillic"],
});

export const metadata: Metadata = {
    title: "Document Management",
    description: "Управление документами",
};

export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode;
}) {
    return (
        <html lang="ru" className={inter.variable}>
            <body className="min-h-screen bg-gray-900">
                <main className="container mx-auto px-4 py-8">
                    <AuthProvider>
                        <DocumentsProvider>
                            <div className="flex">
                                <Sidebar />
                                <main className="flex-1 ml-64 min-h-screen p-8">
                                    {children}
                                </main>
                            </div>
                        </DocumentsProvider>
                    </AuthProvider>
                </main>
            </body>
        </html>
    );
}