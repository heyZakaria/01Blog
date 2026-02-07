import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from './user.service';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class SubscriptionService {
    private apiUrl = `${environment.apiBaseUrl}/api/v1/users`;

    constructor(private http: HttpClient) { }

    toggleFollow(userId: string): Observable<{ following: boolean, followersCount: number }> {
        return this.http.post<{ following: boolean, followersCount: number }>(`${this.apiUrl}/${userId}/follow`, {});
    }

    subscribe(userId: string): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${userId}/follow`, {});
    }

    unsubscribe(userId: string): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${userId}/follow`, {});
    }

    getSubscribers(userId: string): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/${userId}/followers`);
    }

    getSubscriptions(userId: string): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/${userId}/following`);
    }

    isFollowing(userId: string): Observable<{ following: boolean }> {
        // Backend doesn't have a specific endpoint for this, but UserController.getUserById returns this info
        // We might not need this if we load the user profile
        return new Observable(observer => {
            observer.next({ following: false }); // Placeholder
            observer.complete();
        });
    }
}
