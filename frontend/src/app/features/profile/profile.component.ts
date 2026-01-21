import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UserService, UserDTO } from '../../services/user.service';
import { PostService, PostDTO } from '../../services/post.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { switchMap } from 'rxjs';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        RouterLink
    ],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
    user: UserDTO | null = null;
    posts: PostDTO[] = [];
    isLoading = true;
    error: string | null = null;

    constructor(
        private route: ActivatedRoute,
        private userService: UserService,
        private postService: PostService
    ) { }

    ngOnInit() {
        this.route.paramMap.pipe(
            switchMap(params => {
                const userId = params.get('id');
                this.isLoading = true;
                this.error = null;
                if (!userId) throw new Error('User ID not found');
                return this.userService.getUserById(userId);
            })
        ).subscribe({
            next: (user) => {
                this.user = user;
                this.loadPosts(user.id);
            },
            error: (err) => {
                console.error('Failed to load profile', err);
                this.error = 'Failed to load profile';
                this.isLoading = false;
            }
        });
    }

    loadPosts(userId: string) {
        this.postService.getUserPosts(userId).subscribe({
            next: (posts) => {
                this.posts = posts;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Failed to load user posts', err);
                this.isLoading = false;
            }
        });
    }
}
