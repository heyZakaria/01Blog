import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService, UserDTO } from '../../services/user.service';
import { NotificationDropdownComponent } from '../../shared/notification-dropdown/notification-dropdown.component';
import { SubscriptionService } from '../../services/subscription.service';

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
    discoverUsers: UserDTO[] = [];
    loadingDiscover: boolean = false;
    followLoadingIds = new Set<string>();

    constructor(
        private authService: AuthService,
        private userService: UserService,
        private subscriptionService: SubscriptionService,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        // Try to get user from local storage first for immediate display
        this.currentUser = this.userService.getCurrentUser();

        // Then fetch fresh data from API
        this.loadCurrentUser();
        this.loadDiscoverUsers();
    }

    loadCurrentUser() {
        this.userService.getCurrentUserObservable().subscribe({
            next: (user) => {
                this.currentUser = user;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error loading current user:', error);
                this.cdr.detectChanges();
            }
        });
    }

    loadDiscoverUsers() {
        this.loadingDiscover = true;
        this.userService.getDiscoverUsers().subscribe({
            next: (users) => {
                this.discoverUsers = users;
                this.loadingDiscover = false;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error loading discover users:', error);
                this.loadingDiscover = false;
                this.cdr.detectChanges();
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

    getInitials(name?: string): string {
        if (!name) return '?';
        const names = name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }

    followUser(userId: string) {
        if (this.followLoadingIds.has(userId)) {
            return;
        }
        this.followLoadingIds.add(userId);
        this.subscriptionService.toggleFollow(userId).subscribe({
            next: (response) => {
                if (response.following) {
                    this.discoverUsers = this.discoverUsers.filter(u => u.id !== userId);
                }
                this.followLoadingIds.delete(userId);
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error following user:', error);
                this.followLoadingIds.delete(userId);
                this.cdr.detectChanges();
            }
        });
    }

    isFollowLoading(userId: string): boolean {
        return this.followLoadingIds.has(userId);
    }

    navigateToProfile() {
        this.showUserMenu = false;
        if (this.currentUser?.id) {
            this.router.navigate(['/profile', this.currentUser.id]);
        }
    }
}
