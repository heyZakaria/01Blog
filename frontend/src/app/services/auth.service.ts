import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
    email?: string;
    password?: string;
}

export interface RegisterRequest {
    name?: string;
    email?: string;
    password?: string;
    role?: string;
}

export interface LoginResponse {
    token: string;
    user: any;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/v1/auth';
    private tokenKey = 'auth_token';
    private userKey = 'auth_user';

    constructor(private http: HttpClient) { }

    register(data: RegisterRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/register`, data).pipe(
            tap(response => this.saveSession(response))
        );
    }

    login(data: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data).pipe(
            tap(response => this.saveSession(response))
        );
    }

    logout(): void {
        if (typeof localStorage !== 'undefined') {
            localStorage.removeItem(this.tokenKey);
            localStorage.removeItem(this.userKey);
        }
    }

    getToken(): string | null {
        if (typeof localStorage !== 'undefined') {
            return localStorage.getItem(this.tokenKey);
        }
        return null;
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    private saveSession(response: LoginResponse): void {
        if (typeof localStorage !== 'undefined') {
            localStorage.setItem(this.tokenKey, response.token);
            if (response.user) {
                localStorage.setItem(this.userKey, JSON.stringify(response.user));
            }
        }
    }
}
