"use client";

import React, {createContext, ReactNode, useContext, useEffect, useState} from "react";
import {getAccessToken, getRefreshToken, getUser, setAuthData} from "@/services/StorageService";
import {AuthResponse, UserResponseDto} from "@/types/types";
import {AuthService} from "@/services/AuthService";

interface AuthContextType {
    accessToken: string | null;
    refreshToken: string | null;
    user: UserResponseDto | null;
    isAuthenticated: boolean;
    login: (authResponse: AuthResponse, user: UserResponseDto) => void;
    refresh: () => Promise<AuthResponse>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [accessToken, setAccessTokenState] = useState<string | null>(() => getAccessToken());
    const [refreshToken, setRefreshTokenState] = useState<string | null>(() => getRefreshToken());
    const [user, setUserState] = useState<UserResponseDto | null>(() => getUser());
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => !!getAccessToken() && !!getRefreshToken());

    const login = (authResponse: AuthResponse) => {
        const { accessToken, refreshToken, user } = authResponse;
        console.log(user)
        setAuthData(accessToken, refreshToken, user);
        setAccessTokenState(accessToken);
        setRefreshTokenState(refreshToken);
        setUserState(user);
        setIsAuthenticated(true);
    };

    const refresh = async (): Promise<AuthResponse> => {
        try {
            const response = await AuthService.refresh();
            const { accessToken, refreshToken: newRefreshToken, user: updatedUser } = response;
            setAuthData(accessToken, newRefreshToken, updatedUser || user);
            setAccessTokenState(accessToken);
            setRefreshTokenState(newRefreshToken);
            setUserState(updatedUser || user);
            setIsAuthenticated(true);
            return response;
        } catch (error) {
            logout();
            throw error;
        }
    };

    const logout = () => {
        AuthService.logout();
        setAccessTokenState(null);
        setRefreshTokenState(null);
        setUserState(null);
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                refreshToken,
                user,
                isAuthenticated,
                login,
                refresh,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};