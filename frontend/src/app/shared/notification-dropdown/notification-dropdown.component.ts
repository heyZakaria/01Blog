import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService, NotificationDTO } from '../../services/notification.service';
import { interval, Subscription } from 'rxjs';

@Component({
    selector: 'app-notification-dropdown',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './notification-dropdown.component.html',
    styleUrls: ['./notification-dropdown.component.css']
})
export class NotificationDropdownComponent implements OnInit, OnDestroy {
    notifications: NotificationDTO[] = [];
    unreadCount: number = 0;
    isOpen: boolean = false;
    loading: boolean = false;
    private pollSubscription?: Subscription;

    constructor(
        private notificationService: NotificationService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.loadNotifications();
        this.loadUnreadCount();
        // Poll for new notifications every 30 seconds
        this.pollSubscription = interval(30000).subscribe(() => {
            this.loadUnreadCount();
        });
    }

    ngOnDestroy() {
        this.pollSubscription?.unsubscribe();
    }

    toggleDropdown() {
        this.isOpen = !this.isOpen;
        if (this.isOpen) {
            this.loadNotifications();
        }
    }

    loadNotifications() {
        this.loading = true;
        this.notificationService.getNotifications().subscribe({
            next: (notifications) => {
                this.notifications = notifications.slice(0, 10); // Show latest 10
                this.loading = false;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error loading notifications:', error);
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }

    loadUnreadCount() {
        this.notificationService.getUnreadCount().subscribe({
            next: (response) => {
                this.unreadCount = response.count;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error loading unread count:', error);
                this.cdr.detectChanges();
            }
        });
    }

    markAsRead(notification: NotificationDTO) {
        if (notification.read) return;

        this.notificationService.markAsRead(notification.id).subscribe({
            next: () => {
                notification.read = true;
                this.loadUnreadCount();
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error marking notification as read:', error);
                this.cdr.detectChanges();
            }
        });
    }

    markAllAsRead() {
        this.notificationService.markAllAsRead().subscribe({
            next: () => {
                this.notifications.forEach(n => n.read = true);
                this.unreadCount = 0;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error marking all as read:', error);
                this.cdr.detectChanges();
            }
        });
    }

    deleteNotification(notificationId: string, event: Event) {
        event.stopPropagation();
        this.notificationService.deleteNotification(notificationId).subscribe({
            next: () => {
                this.notifications = this.notifications.filter(n => n.id !== notificationId);
                this.loadUnreadCount();
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error deleting notification:', error);
                this.cdr.detectChanges();
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
