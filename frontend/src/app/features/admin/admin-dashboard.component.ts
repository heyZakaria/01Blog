import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { UserService, UserDTO } from '../../services/user.service';
import { ReportService, ReportDTO } from '../../services/report.service';

@Component({
    selector: 'app-admin-dashboard',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './admin-dashboard.component.html',
    styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
    users: UserDTO[] = [];
    reports: ReportDTO[] = [];
    analytics = {
        pendingReports: 0,
        totalUsers: 0
    };
    loading: boolean = false;
    activeTab: 'users' | 'reports' = 'users';

    constructor(
        private adminService: AdminService,
        private userService: UserService,
        private reportService: ReportService
    ) { }

    ngOnInit() {
        this.loadAnalytics();
        this.loadUsers();
        this.loadReports();
    }

    loadAnalytics() {
        this.adminService.getAnalytics().subscribe({
            next: (data) => {
                this.analytics = data;
            },
            error: (error) => {
                console.error('Error loading analytics:', error);
            }
        });
    }

    loadUsers() {
        this.loading = true;
        this.adminService.getAllUsers().subscribe({
            next: (users) => {
                this.users = users;
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading users:', error);
                this.loading = false;
            }
        });
    }

    loadReports() {
        this.adminService.getAllReports().subscribe({
            next: (reports) => {
                this.reports = reports;
            },
            error: (error) => {
                console.error('Error loading reports:', error);
            }
        });
    }

    toggleBanUser(userId: string) {
        if (!confirm('Are you sure you want to ban/unban this user?')) return;

        this.adminService.toggleBanUser(userId).subscribe({
            next: () => {
                this.loadUsers();
            },
            error: (error) => {
                console.error('Error toggling ban:', error);
            }
        });
    }

    deleteUser(userId: string) {
        if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) return;

        this.adminService.deleteUser(userId).subscribe({
            next: () => {
                this.users = this.users.filter(u => u.id !== userId);
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error deleting user:', error);
            }
        });
    }

    resolveReport(reportId: string, action: string) {
        this.adminService.resolveReport(reportId, { action }).subscribe({
            next: () => {
                this.loadReports();
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error resolving report:', error);
            }
        });
    }

    deleteReport(reportId: string) {
        if (!confirm('Delete this report?')) return;

        this.adminService.deleteReport(reportId).subscribe({
            next: () => {
                this.reports = this.reports.filter(r => r.id !== reportId);
                this.loadAnalytics();
            },
            error: (error) => {
                console.error('Error deleting report:', error);
            }
        });
    }

    setActiveTab(tab: 'users' | 'reports') {
        this.activeTab = tab;
    }
}
