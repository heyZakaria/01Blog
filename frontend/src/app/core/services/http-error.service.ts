// Purpose: Global HTTP error message state.
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class HttpErrorService {
    // Stream: shared state for subscribers.
    private readonly messageSubject = new BehaviorSubject<string | null>(null);
    readonly message$ = this.messageSubject.asObservable();
    private hideTimer: ReturnType<typeof setTimeout> | null = null;

    show(message: string, ttlMs = 7000): void {
        if (!message) {
            return;
        }
        this.messageSubject.next(message);
        if (this.hideTimer) {
            clearTimeout(this.hideTimer);
            this.hideTimer = null;
        }
        if (ttlMs > 0) {
            this.hideTimer = setTimeout(() => this.clear(), ttlMs);
        }
    }

    clear(): void {
        if (this.hideTimer) {
            clearTimeout(this.hideTimer);
            this.hideTimer = null;
        }
        this.messageSubject.next(null);
    }
}
