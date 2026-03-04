// Purpose: Report API service.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReportDTO {
    id: string;
    reason: string;
    reporter: {
        id: string;
        name: string;
    };
    reportedUser: {
        id: string;
        name: string;
    };
    status: string;
    createdAt: string;
}

export interface CreateReportRequest {
    reason: string;
}

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class ReportService {
    // Config: base API endpoint.
    private apiUrl = `${environment.apiBaseUrl}/users`;

    // Constructor: injects dependencies.
    constructor(private http: HttpClient) { }

    createReport(userId: string, data: CreateReportRequest): Observable<ReportDTO> {
        return this.http.post<ReportDTO>(`${this.apiUrl}/${userId}/report`, data);
    }
}
