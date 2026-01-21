import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
    { path: 'login', canActivate: [guestGuard], loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent) },
    { path: 'register', canActivate: [guestGuard], loadComponent: () => import('./auth/register/register.component').then(m => m.RegisterComponent) },

    {
        path: '',
        canActivate: [authGuard],
        loadComponent: () => import('./layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
        children: [
            { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
            { path: 'profile/:id', loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent) },
            { path: 'create-post', loadComponent: () => import('./features/create-post/create-post.component').then(m => m.CreatePostComponent) }
        ]
    },

    { path: '**', redirectTo: '' }
];
