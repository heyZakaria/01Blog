import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserDTO {
    id: string;
    name: string;
    email: string;
    role?: string;
    followersCount: number;
    followingCount: number;
    isFollowedByCurrentUser: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = `${environment.apiBaseUrl}/api/v1/users`;

    constructor(private http: HttpClient) { }

    getUserById(id: string): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/${id}`);
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
