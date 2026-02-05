import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    email: string = '';
    password: string = '';
    loading: boolean = false;
    error: string = '';

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    login() {
        if (!this.email || !this.password) {
            this.error = 'Email and password are required';
            return;
        }

        this.loading = true;
        this.error = '';

        this.authService.login({
            email: this.email,
            password: this.password
        }).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(['/']);
            },
            error: (error) => {
                console.error('Login error:', error);
                this.error = error.error?.message || 'Login failed';
                this.loading = false;
            }
        });
    }
}
