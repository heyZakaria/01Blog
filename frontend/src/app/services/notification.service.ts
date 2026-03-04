// Purpose: Notification API service.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface NotificationDTO {
    id: string;
    type: string;
    message: string;
    read: boolean;
    createdAt: string;
    relatedPostId?: string;
    relatedUserId?: string;
}

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class NotificationService {
    // Config: base API endpoint.
    private apiUrl = `${environment.apiBaseUrl}/notifications`;

    // Constructor: injects dependencies.
    constructor(private http: HttpClient) { }

    getNotifications(): Observable<NotificationDTO[]> {
        return this.http.get<NotificationDTO[]>(this.apiUrl);
    }

    getUnreadNotifications(): Observable<NotificationDTO[]> {
        return this.http.get<NotificationDTO[]>(`${this.apiUrl}/unread`);
    }

    getUnreadCount(): Observable<{ count: number }> {
        return this.http.get<{ count: number }>(`${this.apiUrl}/unread/count`);
    }

    markAsRead(notificationId: string): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/${notificationId}/read`, {});
    }

    markAsUnread(notificationId: string): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/${notificationId}/unread`, {});
    }

    markAllAsRead(): Observable<void> {
        return this.http.put<void>(`${this.apiUrl}/read-all`, {});
    }

    deleteNotification(notificationId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${notificationId}`);
    }

    deleteAllNotifications(): Observable<void> {
        return this.http.delete<void>(this.apiUrl);
    }
}
