import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import {UserResponseDto, UserUpdateDto} from '@/types/types';
import { UserService } from '@/services/UserService'; // Предполагаю путь
import { useAuth } from '@/contexts/AuthContext'; // Если нужно синхронизировать с auth

interface UserContextType {
    user: UserResponseDto | null;
    loadUser: (id: number) => Promise<void>;
    updateUser: (id: number, dto: UserUpdateDto) => Promise<void>;
    suspendUser: (id: number) => Promise<void>;
    activateUser: (id: number) => Promise<void>;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<UserResponseDto | null>(null);
    const { isAuthenticated } = useAuth();

    useEffect(() => {
        // Опционально: Автозагрузка current user при mount, если есть userId из auth или storage
        // Например: if (isAuthenticated) { loadUser(currentUserId); }
    }, [isAuthenticated]);

    const loadUser = async (id: number): Promise<void> => {
        try {
            const fetchedUser = await UserService.getUserById(id);
            setUser(fetchedUser);
        } catch (error) {
            console.error('Failed to load user');
        }
    };

    const updateUser = async (id: number, dto: UserUpdateDto): Promise<void> => {
        try {
            const updatedUser = await UserService.updateUser(id, dto);
            setUser(updatedUser);
        } catch (error) {
            console.error('Failed to update user');
        }
    };

    const suspendUser = async (id: number): Promise<void> => {
        try {
            await UserService.suspendUser(id);
            if (user && user.id === id) {
                setUser({ ...user, status: 'SUSPENDED' });
            }
        } catch (error) {
            console.error('Failed to suspend user');
        }
    };

    const activateUser = async (id: number): Promise<void> => {
        try {
            await UserService.activateUser(id);
            if (user && user.id === id) {
                setUser({ ...user, status: 'ACTIVE' });
            }
        } catch (error) {
            console.error('Failed to activate user');
        }
    };

    return (
        <UserContext.Provider
            value={{
                user,
                loadUser,
                updateUser,
                suspendUser,
                activateUser,
            }}
        >
            {children}
        </UserContext.Provider>
    );
};

export const useUser = (): UserContextType => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};