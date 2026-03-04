// Purpose: User API and current-user service.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserDTO {
    id: string;
    name: string;
    email: string;
    role?: string;
    banned?: boolean;
    followersCount: number;
    followingCount: number;
    isFollowedByCurrentUser: boolean;
}

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class UserService {
    // Config: base API endpoint.
    private apiUrl = `${environment.apiBaseUrl}/users`;

    // Constructor: injects dependencies.
    constructor(private http: HttpClient) { }

    getUserById(id: string): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/${id}`);
    }

    getPublicUserById(id: string): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${environment.apiBaseUrl}/public/users/${id}`);
    }

    getCurrentUser(): UserDTO | null {
        if (typeof localStorage !== 'undefined') {
            const raw = localStorage.getItem('auth_user');
            return raw ? JSON.parse(raw) : null;
        }
        return null;
    }

    getCurrentUserObservable(): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/me`);
    }

    getDiscoverUsers(): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/discover`);
    }

    updateProfile(data: Partial<UserDTO>): Observable<UserDTO> {
        const currentUser = this.getCurrentUser();
        if (!currentUser?.id) {
            throw new Error('User not authenticated');
        }
        return this.http.put<UserDTO>(`${this.apiUrl}/${currentUser.id}`, data);
    }
}
