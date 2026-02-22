// Purpose: Admin dashboard component.
import { Component, OnInit, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { UserDTO } from '../../services/user.service';
import { ReportDTO } from '../../services/report.service';
import { PostDTO } from '../../services/post.service';
import { DialogService } from '../../core/services/dialog.service';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
    selector: 'app-admin-dashboard',
    imports: [CommonModule, RouterModule, MatButtonModule, MatTabsModule],
    templateUrl: './admin-dashboard.component.html',
    styleUrls: ['./admin-dashboard.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class AdminDashboardComponent implements OnInit {
    readonly users = signal<UserDTO[]>([]);
    readonly reports = signal<ReportDTO[]>([]);
    readonly posts = signal<PostDTO[]>([]);
    // State: reactive value for the template.
    readonly analytics = signal({
        pendingReports: 0,
        totalUsers: 0,
        totalPosts: 0
    });
    // State: reactive value for the template.
    readonly loading = signal(false);
    readonly activeTab = signal<'users' | 'reports' | 'posts'>('users');
    readonly isUsersTab = computed(() => this.activeTab() === 'users');
    readonly isReportsTab = computed(() => this.activeTab() === 'reports');
    readonly isPostsTab = computed(() => this.activeTab() === 'posts');
    readonly hasReports = computed(() => this.reports().length > 0);
    readonly hasPosts = computed(() => this.posts().length > 0);

    // Constructor: injects dependencies.
    constructor(
        private adminService: AdminService,
        private dialogService: DialogService
    ) { }

    // Angular lifecycle: ng on init.
    ngOnInit() {
        this.loadAnalytics();
        this.loadUsers();
        this.loadReports();
        this.loadPosts();
    }

    // Loads  analytics.
    loadAnalytics() {
        this.adminService.getAnalytics().subscribe({
            next: (data) => {
                this.analytics.set({
                    pendingReports: data.pendingReports ?? 0,
                    totalUsers: data.totalUsers ?? 0,
                    totalPosts: data.totalPosts ?? 0
                });
            },
            error: (error) => {
                console.error('Error loading analytics:', error);
            }
        });
    }

    // Loads  users.
    loadUsers() {
        this.loading.set(true);
        this.adminService.getAllUsers().subscribe({
            next: (users) => {
                this.users.set(users);
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error loading users:', error);
                this.loading.set(false);
            }
        });
    }

    // Loads  reports.
    loadReports() {
        this.adminService.getAllReports().subscribe({
            next: (reports) => {
                this.reports.set(reports);
            },
            error: (error) => {
                console.error('Error loading reports:', error);
            }
        });
    }

    // Loads  posts.
    loadPosts() {
        this.adminService.getAllPosts().subscribe({
            next: (posts) => {
                this.posts.set(posts);
            },
            error: (error) => {
                console.error('Error loading posts:', error);
            }
        });
    }



    // Toggles ban user.
    async toggleBanUser(userId: string) {
        const confirmed = await this.dialogService.confirm(
            'Confirm Action',
            'Are you sure you want to ban/unban this user?',
            'Continue'
        );
        if (!confirmed) return;

        this.adminService.toggleBanUser(userId).subscribe({
            next: () => {
                this.loadUsers();
            },
            error: (error) => {
                console.error('Error toggling ban:', error);
            }
        });
    }

    // Deletes user.
    async deleteUser(userId: string) {
        const confirmed = await this.dialogService.confirm(
            'Delete User',
            'Are you sure you want to delete this user? This action cannot be undone.',
            'Delete'
        );
        if (!confirmed) return;

        this.adminService.deleteUser(userId).subscribe({
            next: () => {
                this.users.update((users) => users.filter(u => u.id !== userId));
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error deleting user:', error);
            }
        });
    }

    // Method: resolve report.
    resolveReport(reportId: string, status: 'RESOLVED' | 'DISMISSED', banUser = false) {
        this.adminService.resolveReport(reportId, { status, banUser }).subscribe({
            next: () => {
                this.loadReports();
                this.loadUsers();
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error resolving report:', error);
            }
        });
    }

    // Deletes report.
    async deleteReport(reportId: string) {
        const confirmed = await this.dialogService.confirm(
            'Delete Report',
            'Delete this report?',
            'Delete'
        );
        if (!confirmed) return;

        this.adminService.deleteReport(reportId).subscribe({
            next: () => {
                this.reports.update((reports) => reports.filter(r => r.id !== reportId));
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error deleting report:', error);
            }
        });
    }

    // Toggles post visibility.
    togglePostVisibility(post: PostDTO) {
        const request$ = post.hidden ? this.adminService.unhidePost(post.id) : this.adminService.hidePost(post.id);
        request$.subscribe({
            next: (updated) => {
                this.posts.update((posts) =>
                    posts.map((item) => (item.id === updated.id ? { ...item, hidden: updated.hidden } : item))
                );
            },
            error: (error) => {
                console.error('Error toggling post visibility:', error);
            }
        });
    }

    // Deletes post.
    async deletePost(postId: string) {
        const confirmed = await this.dialogService.confirm(
            'Delete Post',
            'Are you sure you want to permanently delete this post?',
            'Delete'
        );
        if (!confirmed) return;

        this.adminService.deletePost(postId).subscribe({
            next: () => {
                this.posts.update((posts) => posts.filter((p) => p.id !== postId));
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error deleting post:', error);
            }
        });
    }

    // Sets active tab.
    setActiveTab(tab: 'users' | 'reports' | 'posts') {
        this.activeTab.set(tab);
    }
}
