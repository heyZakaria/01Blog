// Purpose: Notification dropdown component.
import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService, NotificationDTO } from '../../services/notification.service';
import { interval, Subscription } from 'rxjs';

@Component({
    selector: 'app-notification-dropdown',
    imports: [CommonModule, RouterModule],
    templateUrl: './notification-dropdown.component.html',
    styleUrls: ['./notification-dropdown.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class NotificationDropdownComponent implements OnInit, OnDestroy {
    readonly notifications = signal<NotificationDTO[]>([]);
    // State: reactive value for the template.
    readonly unreadCount = signal(0);
    // State: reactive value for the template.
    readonly isOpen = signal(false);
    // State: reactive value for the template.
    readonly loading = signal(false);
    readonly hasNotifications = computed(() => this.notifications().length > 0);
    readonly hasUnread = computed(() => this.unreadCount() > 0);
    private pollSubscription?: Subscription;

    // Constructor: injects dependencies.
    constructor(
        private notificationService: NotificationService
    ) { }

    // Angular lifecycle: ng on init.
    ngOnInit() {
        this.loadNotifications();
        this.loadUnreadCount();
        // Poll for new notifications every 30 seconds
        this.pollSubscription = interval(30000).subscribe(() => {
            this.loadUnreadCount();
        });
    }

    // Angular lifecycle: ng on destroy.
    ngOnDestroy() {
        this.pollSubscription?.unsubscribe();
    }

    // Toggles dropdown.
    toggleDropdown() {
        this.isOpen.update((open) => !open);
        if (this.isOpen()) {
            this.loadNotifications();
        }
    }

    // Loads  notifications.
    loadNotifications() {
        this.loading.set(true);
        this.notificationService.getNotifications().subscribe({
            next: (notifications) => {
                this.notifications.set(notifications.slice(0, 10)); // Show latest 10
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error loading notifications:', error);
                this.loading.set(false);
            }
        });
    }

    // Loads  unread count.
    loadUnreadCount() {
        this.notificationService.getUnreadCount().subscribe({
            next: (response) => {
                this.unreadCount.set(response.count);
            },
            error: (error) => {
                console.error('Error loading unread count:', error);
            }
        });
    }

    // Marks as read.
    markAsRead(notification: NotificationDTO) {
        if (notification.read) return;

        this.notificationService.markAsRead(notification.id).subscribe({
            next: () => {
                this.notifications.update((notifications) =>
                    notifications.map((item) =>
                        item.id === notification.id ? { ...item, read: true } : item
                    )
                );
                this.loadUnreadCount();
            },
            error: (error) => {
                console.error('Error marking notification as read:', error);
            }
        });
    }

    // Marks as unread.
    markAsUnread(notification: NotificationDTO, event: Event) {
        event.stopPropagation();
        if (!notification.read) return;

        this.notificationService.markAsUnread(notification.id).subscribe({
            next: () => {
                this.notifications.update((notifications) =>
                    notifications.map((item) =>
                        item.id === notification.id ? { ...item, read: false } : item
                    )
                );
                this.loadUnreadCount();
            },
            error: (error) => {
                console.error('Error marking notification as unread:', error);
            }
        });
    }

    // Marks all as read.
    markAllAsRead() {
        this.notificationService.markAllAsRead().subscribe({
            next: () => {
                this.notifications.update((notifications) =>
                    notifications.map((item) => ({ ...item, read: true }))
                );
                this.unreadCount.set(0);
            },
            error: (error) => {
                console.error('Error marking all as read:', error);
            }
        });
    }

    // Deletes notification.
    deleteNotification(notificationId: string, event: Event) {
        event.stopPropagation();
        this.notificationService.deleteNotification(notificationId).subscribe({
            next: () => {
                this.notifications.update((notifications) => notifications.filter(n => n.id !== notificationId));
                this.loadUnreadCount();
            },
            error: (error) => {
                console.error('Error deleting notification:', error);
            }
        });
    }

    formatDate(dateString: string): string {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now.getTime() - date.getTime();
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 60) {
            return `${diffMins}m ago`;
        } else if (diffHours < 24) {
            return `${diffHours}h ago`;
        } else if (diffDays < 7) {
            return `${diffDays}d ago`;
        } else {
            return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        }
    }
}
