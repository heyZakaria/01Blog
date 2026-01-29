import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterOutlet, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [
        CommonModule,
        MatToolbarModule,
        MatButtonModule,
        MatIconModule,
        RouterOutlet,
        RouterLink
    ],
    templateUrl: './main-layout.component.html',
    styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
    private authService = inject(AuthService);
    private router = inject(Router);

    readonly isAuthenticated$ = this.authService.isAuthenticated$;
    readonly currentUser$ = this.authService.currentUser$;

    logout(): void {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
