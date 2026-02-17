"use client";

import { useState, useEffect } from 'react';
import { UserService } from '@/services/UserService';
import { useAuth } from '@/contexts/AuthContext';
import { UserResponseDto } from '@/types/types';

const UsersPage = () => {
    const { user: currentUser } = useAuth();
    const [users, setUsers] = useState<UserResponseDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        console.log(currentUser);
        if (currentUser?.role !== 'ADMIN') {
            setError('Access denied. Only admins can view this page.');
            setLoading(false);
            return;
        }

        async function fetchUsers() {
            try {
                const data = await UserService.getAllUsers();
                setUsers(data);
            } catch (err) {
                setError('Failed to load users.');
            } finally {
                setLoading(false);
            }
        }

        fetchUsers();
    }, [currentUser]);

    const handleSuspend = async (id: number) => {
        try {
            await UserService.suspendUser(id);
            setUsers(users.map(u => u.id === id ? { ...u, status: 'SUSPENDED' } : u));
        } catch (err) {
            alert('Failed to suspend user.');
        }
    };

    const handleActivate = async (id: number) => {
        try {
            await UserService.activateUser(id);
            setUsers(users.map(u => u.id === id ? { ...u, status: 'ACTIVE' } : u));
        } catch (err) {
            alert('Failed to activate user.');
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error} </div>;

    return (
        <div className="p-6 bg-gray-900 min-h-screen text-gray-100">
            <h1 className="text-3xl font-bold mb-8 text-white">Все пользователи</h1>

            <div className="overflow-x-auto rounded-lg shadow-xl border border-gray-700">
                <table className="w-full min-w-max table-auto bg-gray-800">
                    <thead>
                    <tr className="bg-gray-750 text-left text-xs font-semibold uppercase tracking-wider text-gray-400">
                        <th className="px-6 py-4">ID</th>
                        <th className="px-6 py-4">Имя пользователя</th>
                        <th className="px-6 py-4">Email</th>
                        <th className="px-6 py-4">Роль</th>
                        <th className="px-6 py-4 text-center">Статус</th>
                        <th className="px-6 py-4 text-center">Действия</th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-700">
                    {users.map((user) => (
                        <tr
                            key={user.id}
                            className="hover:bg-gray-750/50 transition-colors duration-200"
                        >
                            <td className="px-6 py-5 text-sm font-medium text-gray-300">
                                {user.id}
                            </td>
                            <td className="px-6 py-5 text-sm text-gray-100">
                                {user.username}
                            </td>
                            <td className="px-6 py-5 text-sm text-gray-300">
                                {user.email}
                            </td>
                            <td className="px-6 py-5">
                                    <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full
                                        ${user.role === 'ADMIN'
                                        ? 'bg-purple-900/30 text-purple-400'
                                        : 'bg-blue-900/30 text-blue-400'
                                    }`}
                                    >
                                        {user.role}
                                    </span>
                            </td>
                            <td className="px-6 py-5 text-center">
                                    <span className={`inline-flex px-3 py-1 text-xs font-semibold rounded-full
                                        ${user.status === 'ACTIVE'
                                        ? 'bg-green-900/30 text-green-400'
                                        : 'bg-red-900/30 text-red-400'
                                    }`}
                                    >
                                        {user.status === 'ACTIVE' ? 'Активен' : 'Заблокирован'}
                                    </span>
                            </td>
                            <td className="px-6 py-5 text-center space-x-2">
                                {user.status !== 'SUSPENDED' ? (
                                    <button
                                        onClick={() => handleSuspend(user.id)}
                                        className="inline-flex items-center gap-1 bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-md transition shadow-sm"
                                    >
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8 7a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1zm4 0a1 1 0 00-1 1v4a1 1 0 102 0V8a1 1 0 00-1-1z"/></svg>
                                        Заблокировать
                                    </button>
                                ) : (
                                    <button
                                        onClick={() => handleActivate(user.id)}
                                        className="inline-flex items-center gap-1 bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded-md transition shadow-sm"
                                    >
                                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"/></svg>
                                        Активировать
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                {users.length === 0 && (
                    <div className="text-center py-12 text-gray-500">
                        Пользователей пока нет
                    </div>
                )}
            </div>
        </div>
    );
};

export default UsersPage;