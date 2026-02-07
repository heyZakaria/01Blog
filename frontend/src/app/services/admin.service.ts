import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from './user.service';
import { PostDTO } from './post.service';
import { ReportDTO } from './report.service';
import { environment } from '../../environments/environment';

export interface ResolveReportRequest {
    action: string;
    notes?: string;
}

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private apiUrl = `${environment.apiBaseUrl}/api/v1/admin`;

    constructor(private http: HttpClient) { }

    // User Management
    getAllUsers(): Observable<UserDTO[]> {
        return this.http.get<UserDTO[]>(`${this.apiUrl}/users`);
    }

    toggleBanUser(userId: string): Observable<{ userId: string; banned: boolean; message: string }> {
        return this.http.put<{ userId: string; banned: boolean; message: string }>(`${this.apiUrl}/users/${userId}/ban`, {});
    }

    deleteUser(userId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
    }

    // Report Management
    getAllReports(): Observable<ReportDTO[]> {
        return this.http.get<ReportDTO[]>(`${this.apiUrl}/reports`);
    }

    getReportsByStatus(status: string): Observable<ReportDTO[]> {
        return this.http.get<ReportDTO[]>(`${this.apiUrl}/reports/status/${status}`);
    }

    resolveReport(reportId: string, data: ResolveReportRequest): Observable<ReportDTO> {
        return this.http.put<ReportDTO>(`${this.apiUrl}/reports/${reportId}/resolve`, data);
    }

    deleteReport(reportId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/reports/${reportId}`);
    }

    // Analytics
    getAnalytics(): Observable<{ pendingReports: number; totalUsers: number }> {
        return this.http.get<{ pendingReports: number; totalUsers: number }>(`${this.apiUrl}/analytics`);
    }
}
