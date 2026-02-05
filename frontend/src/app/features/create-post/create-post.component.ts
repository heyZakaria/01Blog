import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService, CreatePostRequest } from '../../services/post.service';

@Component({
    selector: 'app-create-post',
    standalone: true,
    imports: [CommonModule, FormsModule],
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

        this.postService.createPost(request).subscribe({
            next: (post) => {
                // If there's a file, upload it
                if (this.selectedFile) {
                    this.postService.uploadMedia(post.id, this.selectedFile).subscribe({
                        next: () => {
                            this.loading = false;
                            this.router.navigate(['/']);
                        },
                        error: (error) => {
                            console.error('Error uploading media:', error);
                            this.loading = false;
                            // Still navigate even if media upload fails
                            this.router.navigate(['/']);
                        }
                    });
                } else {
                    this.loading = false;
                    this.router.navigate(['/']);
                }
            },
            error: (error) => {
                console.error('Error creating post:', error);
                this.error = error.error?.message || 'Failed to create post';
                this.loading = false;
            }
        });
    }
}
