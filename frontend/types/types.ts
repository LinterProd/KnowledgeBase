export enum UserRole {
    USER = 'USER',
    ADMIN = 'ADMIN',
}

export enum DocumentStatus {
    DRAFT = 'DRAFT',
    PUBLISHED = 'PUBLISHED',
    ARCHIVED = 'ARCHIVED',
}

export interface User  {
    id: number;
    username: string;
    email: string;
    role: UserRole;
    status: string;
}

export interface Document  {
    id: string;
    title: string;
    filePath: string;
    fileType: string | null;
    fileSize: number | null;
    author: User;
    status: DocumentStatus;
    createdAt: string | number | Date;
}

// DTO
export interface UserCreateDto {
    username: string;
    email: string;
    password: string;
}

export interface AuthRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    user: UserResponseDto;
}

export interface UserUpdateDto {
    email: string;
    password: string;
}

export interface UserResponseDto extends User {}

export interface DocumentResponseDto extends Document {}