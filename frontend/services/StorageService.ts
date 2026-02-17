import {UserResponseDto} from "@/types/types";

type StorageKey = 'accessToken' | 'refreshToken' | 'user';

export const STORAGE_KEYS = {
    ACCESS_TOKEN: 'accessToken',
    REFRESH_TOKEN: 'refreshToken',
    USER: 'user',
} as const;

export function setItem(key: StorageKey, value: string): void {
    if (typeof window !== 'undefined') {
        localStorage.setItem(key, value);
    }
}

export function getItem(key: StorageKey): string | null {
    return typeof window !== 'undefined' ? localStorage.getItem(key) : null;
}

export function removeItem(key: StorageKey): void {
    if (typeof window !== 'undefined') {
        localStorage.removeItem(key);
    }
}

export function setAccessToken(token: string): void {
    setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
}

export function getAccessToken(): string | null {
    return getItem(STORAGE_KEYS.ACCESS_TOKEN);
}

export function clearAccessToken(): void {
    removeItem(STORAGE_KEYS.ACCESS_TOKEN);
}

export function setRefreshToken(token: string): void {
    setItem(STORAGE_KEYS.REFRESH_TOKEN, token);
}

export function getRefreshToken(): string | null {
    return getItem(STORAGE_KEYS.REFRESH_TOKEN);
}

export function clearRefreshToken(): void {
    removeItem(STORAGE_KEYS.REFRESH_TOKEN);
}

export function setUser(user: UserResponseDto): void {
    setItem('user', JSON.stringify(user));
}

export function getUser(): UserResponseDto | null {
    const stored = getItem('user');
    return stored ? JSON.parse(stored) : null;
}

export function clearUser(): void {
    removeItem('user');
}

export function setAuthData(accessToken: string, refreshToken: string, user?: UserResponseDto): void {
    setAccessToken(accessToken);
    setRefreshToken(refreshToken);
    if (user) setUser(user);
}

export function clearAllAuthData(): void {
    clearAccessToken();
    clearRefreshToken();
    clearUser();
}