import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';

export const adminGuard = () => {
    const authService = inject(AuthService);
    const userService = inject(UserService);
    const router = inject(Router);

    // Check if authenticated first
    if (!authService.isAuthenticated()) {
        router.navigate(['/login']);
        return false;
    }

    // Check user role from current user
    const currentUser = userService.getCurrentUser();
    const isAdmin = currentUser?.role === 'ADMIN';

    if (isAdmin) {
        return true;
    }

    // Not admin, redirect to home
    router.navigate(['/']);
    return false;
};
