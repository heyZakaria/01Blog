import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {
    name: string = '';
    email: string = '';
    password: string = '';
    loading: boolean = false;
    error: string = '';

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    register() {
        if (!this.name || !this.email || !this.password) {
            this.error = 'All fields are required';
            return;
        }

        this.loading = true;
        this.error = '';

        this.authService.register({
            name: this.name,
            email: this.email,
            password: this.password
        }).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(['/']);
            },
            error: (error) => {
                console.error('Registration error:', error);
                this.error = error.error?.message || 'Registration failed';
                this.loading = false;
            }
        });
    }
}
