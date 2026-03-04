// Purpose: Confirm dialog component.
import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogService } from '../../core/services/dialog.service';

@Component({
    selector: 'app-confirm-dialog',
    imports: [CommonModule],
    templateUrl: './confirm-dialog.component.html',
    styleUrl: './confirm-dialog.component.css',
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class ConfirmDialogComponent {
    private readonly dialogService = inject(DialogService);
    readonly state$ = this.dialogService.state$;

    confirm(): void {
        this.dialogService.close(true);
    }

    cancel(): void {
        this.dialogService.close(false);
    }
}
