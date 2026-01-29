import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

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
    private apiUrl = `${environment.apiBaseUrl}/api/v1/auth`;
    private tokenKey = 'auth_token';
    private userKey = 'auth_user';
    private tokenSubject = new BehaviorSubject<string | null>(this.loadToken());
    private userSubject = new BehaviorSubject<any | null>(this.loadUser());
    readonly token$ = this.tokenSubject.asObservable();
    readonly currentUser$ = this.userSubject.asObservable();
    readonly isAuthenticated$ = new BehaviorSubject<boolean>(!!this.tokenSubject.value);

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
        this.tokenSubject.next(null);
        this.userSubject.next(null);
        this.isAuthenticated$.next(false);
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

    getCurrentUser(): any | null {
        if (typeof localStorage !== 'undefined') {
            const raw = localStorage.getItem(this.userKey);
            return raw ? JSON.parse(raw) : null;
        }
        return null;
    }

    private saveSession(response: LoginResponse): void {
        if (typeof localStorage !== 'undefined') {
            localStorage.setItem(this.tokenKey, response.token);
            if (response.user) {
                localStorage.setItem(this.userKey, JSON.stringify(response.user));
            }
        }
        this.tokenSubject.next(response.token ?? null);
        this.userSubject.next(response.user ?? null);
        this.isAuthenticated$.next(!!response.token);
    }

    private loadToken(): string | null {
        if (typeof localStorage !== 'undefined') {
            return localStorage.getItem(this.tokenKey);
        }
        return null;
    }

    private loadUser(): any | null {
        if (typeof localStorage !== 'undefined') {
            const raw = localStorage.getItem(this.userKey);
            return raw ? JSON.parse(raw) : null;
        }
        return null;
    }
}
