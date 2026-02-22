// Purpose: Main layout shell component.
import { Component, OnInit, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService, UserDTO } from '../../services/user.service';
import { NotificationDropdownComponent } from '../../shared/notification-dropdown/notification-dropdown.component';
import { SubscriptionService } from '../../services/subscription.service';

@Component({
    selector: 'app-main-layout',
    imports: [
        CommonModule,
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        NotificationDropdownComponent
    ],
    templateUrl: './main-layout.component.html',
    styleUrl: './main-layout.component.css',
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class MainLayoutComponent implements OnInit {
    readonly currentUser = signal<UserDTO | null>(null);
    // State: reactive value for the template.
    readonly showUserMenu = signal(false);
    readonly discoverUsers = signal<UserDTO[]>([]);
    // State: reactive value for the template.
    readonly loadingDiscover = signal(false);
    readonly followLoadingIds = signal<Set<string>>(new Set());
    readonly hasDiscoverUsers = computed(() => this.discoverUsers().length > 0);

    // Constructor: injects dependencies.
    constructor(
        private authService: AuthService,
        private userService: UserService,
        private subscriptionService: SubscriptionService,
        private router: Router
    ) { }

    // Angular lifecycle: ng on init.
    ngOnInit() {
        // Try to get user from local storage first for immediate display
        this.currentUser.set(this.userService.getCurrentUser());

        // Then fetch fresh data from API
        this.loadCurrentUser();
        this.loadDiscoverUsers();
    }

    // Loads  current user.
    loadCurrentUser() {
        this.userService.getCurrentUserObservable().subscribe({
            next: (user) => {
                this.currentUser.set(user);
            },
            error: (error) => {
                console.error('Error loading current user:', error);
            }
        });
    }

    // Loads  discover users.
    loadDiscoverUsers() {
        this.loadingDiscover.set(true);
        this.userService.getDiscoverUsers().subscribe({
            next: (users) => {
                this.discoverUsers.set(users);
                this.loadingDiscover.set(false);
            },
            error: (error) => {
                console.error('Error loading discover users:', error);
                this.loadingDiscover.set(false);
            }
        });
    }

    // Toggles user menu.
    toggleUserMenu() {
        this.showUserMenu.update((open) => !open);
    }

    // Handles logout.
    logout() {
        this.authService.logout();
        window.location.href = '/login';
    }

    getUserInitials(): string {
        if (!this.currentUser()?.name) return '?';
        const names = this.currentUser()!.name.split(' ');
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

    // Method: follow user.
    followUser(userId: string) {
        if (this.followLoadingIds().has(userId)) {
            return;
        }
        const next = new Set(this.followLoadingIds());
        next.add(userId);
        this.followLoadingIds.set(next);
        this.subscriptionService.toggleFollow(userId).subscribe({
            next: (response) => {
                if (response.following) {
                    this.discoverUsers.update((users) => users.filter(u => u.id !== userId));
                }
                const nextIds = new Set(this.followLoadingIds());
                nextIds.delete(userId);
                this.followLoadingIds.set(nextIds);
            },
            error: (error) => {
                console.error('Error following user:', error);
                const nextIds = new Set(this.followLoadingIds());
                nextIds.delete(userId);
                this.followLoadingIds.set(nextIds);
            }
        });
    }

    isFollowLoading(userId: string): boolean {
        return this.followLoadingIds().has(userId);
    }

    // Method: navigate to profile.
    navigateToProfile() {
        this.showUserMenu.set(false);
        if (this.currentUser()?.id) {
            this.router.navigate(['/profile', this.currentUser()!.id]);
        }
    }
}
