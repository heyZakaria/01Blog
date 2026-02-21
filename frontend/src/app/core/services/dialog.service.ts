// Purpose: Global confirm/alert dialog state.
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface DialogState {
    open: boolean;
    title: string;
    message: string;
    confirmText: string;
    cancelText: string;
    showCancel: boolean;
    resolve?: (value: boolean) => void;
}

@Injectable({
    providedIn: 'root'
})
// Class: Provides API calls and shared state.
export class DialogService {
    // Stream: shared state for subscribers.
    private readonly stateSubject = new BehaviorSubject<DialogState>({
        open: false,
        title: '',
        message: '',
        confirmText: 'OK',
        cancelText: 'Cancel',
        showCancel: false
    });

    readonly state$ = this.stateSubject.asObservable();

    confirm(title: string, message: string, confirmText = 'Confirm', cancelText = 'Cancel'): Promise<boolean> {
        return new Promise<boolean>((resolve) => {
            this.stateSubject.next({
                open: true,
                title,
                message,
                confirmText,
                cancelText,
                showCancel: true,
                resolve
            });
        });
    }

    alert(title: string, message: string, confirmText = 'OK'): Promise<void> {
        return new Promise<void>((resolve) => {
            this.stateSubject.next({
                open: true,
                title,
                message,
                confirmText,
                cancelText: '',
                showCancel: false,
                resolve: () => resolve()
            });
        });
    }

    close(result: boolean): void {
        const current = this.stateSubject.value;
        if (current.resolve) {
            current.resolve(result);
        }
        this.stateSubject.next({
            open: false,
            title: '',
            message: '',
            confirmText: 'OK',
            cancelText: 'Cancel',
            showCancel: false
        });
    }
}
