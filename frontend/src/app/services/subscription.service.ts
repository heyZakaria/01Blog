// Purpose: Follow/subscription API service.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from './user.service';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class SubscriptionService {
    // Config: base API endpoint.
    private apiUrl = `${environment.apiBaseUrl}/users`;

    // Constructor: injects dependencies.
    constructor(private http: HttpClient) { }

    toggleFollow(userId: string): Observable<{ following: boolean, followersCount: number }> {
        return this.http.post<{ following: boolean, followersCount: number }>(`${this.apiUrl}/${userId}/follow`, {});
    }

    getSubscribers(userId: string): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/${userId}/followers`);
    }

    getSubscriptions(userId: string): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/${userId}/following`);
    }
}
