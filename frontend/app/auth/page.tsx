"use client";

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { toast } from 'react-hot-toast';
import { AuthService } from '@/services/AuthService';
import { useAuth } from '@/contexts/AuthContext';
import { AuthRequest, UserCreateDto } from '@/types/types';
import Sidebar from "@/components/sidebar/Sidebar";

const AuthPage = () => {
    const router = useRouter();
    const { login } = useAuth();
    const [isLogin, setIsLogin] = useState(true);
    const [loading, setLoading] = useState(false);

    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState<{ username?: string; email?: string; password?: string }>({});

    const validateForm = () => {
        const newErrors: typeof errors = {};
        if (!username.trim()) newErrors.username = 'Username is required';
        if (!isLogin && !email.trim()) newErrors.email = 'Email is required';
        if (!isLogin && !/\S+@\S+\.\S+/.test(email)) newErrors.email = 'Invalid email';
        if (!password.trim()) newErrors.password = 'Password is required';
        if (password.length < 5) newErrors.password = 'Password must be at least 6 characters';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!validateForm()) return;
        setLoading(true);
        try {
            let response;
            if (isLogin) {
                response = await AuthService.login({ username, password } as AuthRequest);
            } else {
                response = await AuthService.register({ username, email, password } as UserCreateDto);
            }
            login(response);
            toast.success(`${isLogin ? 'Login' : 'Registration'} successful!`);
            router.push('/');
        } catch (error: any) {
            toast.error(error.response?.data?.message || `${isLogin ? 'Login' : 'Registration'} failed`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
                        {isLogin ? 'Sign in to your account' : 'Create your account'}
                    </h2>
                </div>

                {/* Toggle Tabs */}
                <div className="flex space-x-1 bg-gray-800 rounded-lg">
                    <button
                        onClick={() => setIsLogin(true)}
                        className={`w-full py-2 px-4 text-sm font-medium rounded-lg transition-colors ${
                            isLogin
                                ? 'bg-blue-600 text-white'
                                : 'text-gray-400 hover:text-white hover:bg-gray-700'
                        }`}
                    >
                        Login
                    </button>
                    <button
                        onClick={() => setIsLogin(false)}
                        className={`w-full py-2 px-4 text-sm font-medium rounded-lg transition-colors ${
                            !isLogin
                                ? 'bg-blue-600 text-white'
                                : 'text-gray-400 hover:text-white hover:bg-gray-700'
                        }`}
                    >
                        Register
                    </button>
                </div>

                {/* Form */}
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div>
                        <label htmlFor="username" className="block text-sm font-medium text-gray-300">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-600 rounded-md placeholder-gray-500 text-white bg-gray-800 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                            placeholder="Username"
                        />
                        {errors.username && <p className="mt-1 text-sm text-red-500">{errors.username}</p>}
                    </div>

                    {!isLogin && (
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-300">
                                Email
                            </label>
                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-600 rounded-md placeholder-gray-500 text-white bg-gray-800 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Email"
                            />
                            {errors.email && <p className="mt-1 text-sm text-red-500">{errors.email}</p>}
                        </div>
                    )}

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-300">
                            Password
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-600 rounded-md placeholder-gray-500 text-white bg-gray-800 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                            placeholder="Password"
                        />
                        {errors.password && <p className="mt-1 text-sm text-red-500">{errors.password}</p>}
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={loading}
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {loading ? 'Loading...' : isLogin ? 'Sign in' : 'Sign up'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AuthPage;