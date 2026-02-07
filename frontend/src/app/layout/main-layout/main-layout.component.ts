import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService, UserDTO } from '../../services/user.service';
import { NotificationDropdownComponent } from '../../shared/notification-dropdown/notification-dropdown.component';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [
        CommonModule,
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        NotificationDropdownComponent
    ],
    templateUrl: './main-layout.component.html',
    styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent implements OnInit {
    currentUser: UserDTO | null = null;
    showUserMenu: boolean = false;

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private router: Router
    ) { }

    ngOnInit() {
        // Try to get user from local storage first for immediate display
        this.currentUser = this.userService.getCurrentUser();

        // Then fetch fresh data from API
        this.loadCurrentUser();
    }

    loadCurrentUser() {
        this.userService.getCurrentUserObservable().subscribe({
            next: (user) => {
                this.currentUser = user;
            },
            error: (error) => {
                console.error('Error loading current user:', error);
            }
        });
    }

    toggleUserMenu() {
        this.showUserMenu = !this.showUserMenu;
    }

    logout() {
        this.authService.logout();
        window.location.href = '/login';
    }

    getUserInitials(): string {
        if (!this.currentUser?.name) return '?';
        const names = this.currentUser.name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }

    navigateToProfile() {
        this.showUserMenu = false;
        if (this.currentUser?.id) {
            this.router.navigate(['/profile', this.currentUser.id]);
        }
    }
}
