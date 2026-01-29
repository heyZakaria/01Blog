import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserDTO {
    id: string;
    name: string;
    email: string;
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

    getCurrentUser(): Observable<UserDTO> {
        return this.http.get<UserDTO>(`${this.apiUrl}/me`);
    }
}
