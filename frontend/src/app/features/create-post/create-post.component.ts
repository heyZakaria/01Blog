// Purpose: Create-post page component.
import { Component, ChangeDetectionStrategy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PostService, CreatePostRequest } from '../../services/post.service';
import { catchError, finalize, map, of, switchMap, throwError, timeout } from 'rxjs';

@Component({
    selector: 'app-create-post',
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './create-post.component.html',
    styleUrls: ['./create-post.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class CreatePostComponent {
    // Form model: groups form controls.
    readonly form = new FormGroup({
        title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        description: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });
    readonly selectedFile = signal<File | null>(null);
    readonly filePreview = signal<string | null>(null);
    readonly fileType = signal<'image' | 'video' | null>(null);
    // State: reactive value for the template.
    readonly loading = signal(false);
    // State: reactive value for the template.
    readonly error = signal('');
    // Checks if submit.
    get canSubmit(): boolean {
        return this.form.valid;
    }

    // Constructor: injects dependencies.
    constructor(
        private postService: PostService,
        private router: Router
    ) { }

    // Handles file selected.
    onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Validate file type
            if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
                this.error.set('Please select an image or video file');
                return;
            }

            this.selectedFile.set(file);
            this.fileType.set(file.type.startsWith('image/') ? 'image' : 'video');

            // Create preview
            const reader = new FileReader();
            reader.onload = (e) => {
                this.filePreview.set(e.target?.result as string);
            };
            reader.readAsDataURL(file);
            this.error.set('');
        }
    }

    // Method: remove file.
    removeFile() {
        this.selectedFile.set(null);
        this.filePreview.set(null);
        this.fileType.set(null);
    }

    // Creates post.
    createPost() {
        if (this.loading()) {
            return;
        }
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            this.error.set('Title and description are required');
            return;
        }

        this.loading.set(true);
        this.error.set('');
        this.form.disable();

        const { title, description } = this.form.getRawValue();
        const request: CreatePostRequest = {
            title,
            description
        };

        this.postService.createPost(request).pipe(
            timeout(15000),
            switchMap((post) => {
                if (!this.selectedFile()) {
                    return of(post);
                }
                return this.postService.uploadMedia(post.id, this.selectedFile()!).pipe(
                    timeout(15000),
                    map(() => post)
                );
            }),
            catchError((error) => {
                console.error('Error creating post:', error);
                this.error.set(error?.error?.message || error?.message || 'Failed to create post');
                return throwError(() => error);
            }),
            finalize(() => {
                this.loading.set(false);
                this.form.enable();
            })
        ).subscribe({
            next: () => {
                this.router.navigate(['/']);
            },
            error: () => {
                // Error message already set in catchError.
            }
        });
    }
}
