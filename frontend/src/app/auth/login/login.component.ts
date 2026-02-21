// Purpose: Login page component.
import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css',
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class LoginComponent {
    // State: reactive value for the template.
    readonly loading = signal(false);
    // State: reactive value for the template.
    readonly error = signal('');
    // Form model: groups form controls.
    readonly form = new FormGroup({
        email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
        password: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });
    // Checks if submit.
    get canSubmit(): boolean {
        return this.form.valid;
    }

    // Constructor: injects dependencies.
    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    // Handles login.
    login() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.error.set('Email and password are required');
            return;
        }

        this.loading.set(true);
        this.error.set('');

        this.form.disable();
        const { email, password } = this.form.getRawValue();

        this.authService.login({ email, password }).subscribe({
            next: () => {
                this.loading.set(false);
                this.form.enable();
                this.router.navigate(['/']);
            },
            error: (error) => {
                console.error('Login error:', error);
                this.error.set(error.error?.message || 'Login failed');
                this.loading.set(false);
                this.form.enable();
            }
        });
    }
}
