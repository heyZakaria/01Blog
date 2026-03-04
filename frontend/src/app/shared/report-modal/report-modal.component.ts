// Purpose: Report modal component.
import { Component, ChangeDetectionStrategy, signal, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { ReportService, CreateReportRequest } from '../../services/report.service';
import { DialogService } from '../../core/services/dialog.service';

@Component({
    selector: 'app-report-modal',
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './report-modal.component.html',
    styleUrls: ['./report-modal.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class ReportModalComponent {
    readonly userId = input.required<string>();
    readonly userName = input.required<string>();
    readonly close = output<void>();
    readonly reported = output<void>();

    // State: reactive value for the template.
    readonly loading = signal(false);
    // State: reactive value for the template.
    readonly error = signal('');
    // Form model: groups form controls.
    readonly form = new FormGroup({
        reason: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });
    // Checks if submit.
    get canSubmit(): boolean {
        return this.form.valid;
    }

    // Constructor: injects dependencies.
    constructor(
        private reportService: ReportService,
        private dialogService: DialogService
    ) { }

    // Method: submit report.
    async submitReport() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.error.set('Please provide a reason for reporting');
            return;
        }

        const confirmed = await this.dialogService.confirm(
            'Submit Report',
            'Are you sure you want to submit this report?',
            'Submit'
        );
        if (!confirmed) {
            return;
        }

        this.loading.set(true);
        this.error.set('');
        this.form.disable();

        const { reason } = this.form.getRawValue();
        const request: CreateReportRequest = {
            reason
        };

        this.reportService.createReport(this.userId(), request).subscribe({
            next: () => {
                this.loading.set(false);
                this.reported.emit();
                this.form.enable();
                this.closeModal();
            },
            error: (error) => {
                this.loading.set(false);
                this.error.set(error.error?.message || 'Failed to submit report');
                this.form.enable();
            }
        });
    }

    // Closes modal.
    closeModal() {
        this.close.emit();
    }
}
