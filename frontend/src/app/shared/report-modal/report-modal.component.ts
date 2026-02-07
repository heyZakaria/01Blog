import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService, CreateReportRequest } from '../../services/report.service';

@Component({
    selector: 'app-report-modal',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './report-modal.component.html',
    styleUrls: ['./report-modal.component.css']
})
export class ReportModalComponent {
    @Input() userId!: string;
    @Input() userName!: string;
    @Output() close = new EventEmitter<void>();
    @Output() reported = new EventEmitter<void>();

    reason: string = '';
    loading: boolean = false;
    error: string = '';

    constructor(private reportService: ReportService) { }

    submitReport() {
        if (!this.reason.trim()) {
            this.error = 'Please provide a reason for reporting';
            return;
        }

        this.loading = true;
        this.error = '';

        const request: CreateReportRequest = {
            reportedUserId: this.userId,
            reason: this.reason
        };

        this.reportService.createReport(request).subscribe({
            next: () => {
                this.loading = false;
                this.reported.emit();
                this.closeModal();
            },
            error: (error) => {
                this.loading = false;
                this.error = error.error?.message || 'Failed to submit report';
            }
        });
    }

    closeModal() {
        this.close.emit();
    }
}
