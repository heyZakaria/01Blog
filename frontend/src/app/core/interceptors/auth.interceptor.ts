// Purpose: HTTP auth interceptor and auth error handling.
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { HttpErrorService } from '../services/http-error.service';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

// Handles auth interceptor.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const httpErrorService = inject(HttpErrorService);
    const token = authService.getToken();

    const isAuthRequest = req.url.includes('/api/v1/auth/');

    let request = req;

    if (token) {
        request = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(request).pipe(
        catchError((error: HttpErrorResponse) => {
            const onAuthPage = router.url === '/login' || router.url === '/register';
            if (!isAuthRequest && !onAuthPage && (error.status === 401 || error.status === 403)) {
                authService.logout();
                router.navigate(['/login']);
            }
            const message = extractErrorMessage(error);
            if (message) {
                httpErrorService.show(message);
            }
            return throwError(() => error);
        })
    );
};

// Method: extract error message.
function extractErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
        return 'Network error. Please check your connection.';
    }

    const payload: any = error.error;
    if (payload) {
        if (typeof payload === 'string') {
            return payload;
        }
        if (payload.message) {
            return payload.message;
        }
        if (payload.error) {
            return payload.error;
        }
        if (payload.errors && typeof payload.errors === 'object') {
            const keys = Object.keys(payload.errors);
            if (keys.length > 0) {
                return payload.errors[keys[0]];
            }
        }
    }

    switch (error.status) {
        case 400:
            return 'Bad request. Please check your input.';
        case 401:
            return 'Authentication required.';
        case 403:
            return 'Access denied.';
        case 404:
            return 'Not found.';
        case 409:
            return 'Conflict. Please try again.';
        case 413:
            return 'File too large.';
        case 500:
            return 'Server error. Please try again later.';
        default:
            return 'Something went wrong. Please try again.';
    }
}
