// Purpose: Error banner component.
import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorService } from '../../core/services/http-error.service';

@Component({
    selector: 'app-error-banner',
    imports: [CommonModule],
    templateUrl: './error-banner.component.html',
    styleUrl: './error-banner.component.css',
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class ErrorBannerComponent {
    private readonly httpErrorService = inject(HttpErrorService);
    readonly message$ = this.httpErrorService.message$;

    dismiss(): void {
        this.httpErrorService.clear();
    }
}
