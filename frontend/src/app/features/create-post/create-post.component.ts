import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';

@Component({
    selector: 'app-create-post',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatInputModule,
        MatButtonModule,
        MatFormFieldModule
    ],
    templateUrl: './create-post.component.html',
    styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
    postForm: FormGroup;
    isSubmitting = false;

    constructor(
        private fb: FormBuilder,
        private postService: PostService,
        private router: Router
    ) {
        this.postForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required]
        });
    }

    onSubmit() {
        if (this.postForm.valid) {
            this.isSubmitting = true;
            this.postService.createPost(this.postForm.value).subscribe({
                next: () => {
                    this.router.navigate(['/']);
                },
                error: (err) => {
                    console.error('Failed to create post', err);
                    this.isSubmitting = false;
                }
            });
        }
    }
}
