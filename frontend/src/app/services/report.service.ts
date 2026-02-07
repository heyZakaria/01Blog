import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReportDTO {
    id: string;
    reason: string;
    reportedUserId: string;
    reportedUserName: string;
    reporterId: string;
    reporterName: string;
    status: string;
    createdAt: string;
}

export interface CreateReportRequest {
    reportedUserId: string;
    reason: string;
}

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private apiUrl = `${environment.apiBaseUrl}/api/v1/reports`;

    constructor(private http: HttpClient) { }

    createReport(data: CreateReportRequest): Observable<ReportDTO> {
        return this.http.post<ReportDTO>(this.apiUrl, data);
    }

    getUserReports(): Observable<ReportDTO[]> {
        return this.http.get<ReportDTO[]>(`${this.apiUrl}/user`);
    }
}
