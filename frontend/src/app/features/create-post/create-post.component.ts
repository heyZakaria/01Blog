import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PostService, CreatePostRequest } from '../../services/post.service';
import { catchError, finalize, map, of, switchMap, throwError, timeout } from 'rxjs';

@Component({
    selector: 'app-create-post',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './create-post.component.html',
    styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent {
    title: string = '';
    description: string = '';
    selectedFile: File | null = null;
    filePreview: string | null = null;
    fileType: 'image' | 'video' | null = null;
    loading: boolean = false;
    error: string = '';

    constructor(
        private postService: PostService,
        private router: Router
    ) { }

    onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files[0]) {
            const file = input.files[0];

            // Validate file type
            if (!file.type.startsWith('image/') && !file.type.startsWith('video/')) {
                this.error = 'Please select an image or video file';
                return;
            }

            this.selectedFile = file;
            this.fileType = file.type.startsWith('image/') ? 'image' : 'video';

            // Create preview
            const reader = new FileReader();
            reader.onload = (e) => {
                this.filePreview = e.target?.result as string;
            };
            reader.readAsDataURL(file);
            this.error = '';
        }
    }

    removeFile() {
        this.selectedFile = null;
        this.filePreview = null;
        this.fileType = null;
    }

    createPost() {
        if (this.loading) {
            return;
        }
        if (!this.title.trim() || !this.description.trim()) {
            this.error = 'Title and description are required';
            return;
        }

        this.loading = true;
        this.error = '';

        const request: CreatePostRequest = {
            title: this.title,
            description: this.description
        };

        this.postService.createPost(request).pipe(
            timeout(15000),
            switchMap((post) => {
                if (!this.selectedFile) {
                    return of(post);
                }
                return this.postService.uploadMedia(post.id, this.selectedFile).pipe(
                    timeout(15000),
                    map(() => post)
                );
            }),
            catchError((error) => {
                console.error('Error creating post:', error);
                this.error = error?.error?.message || error?.message || 'Failed to create post';
                return throwError(() => error);
            }),
            finalize(() => {
                this.loading = false;
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
