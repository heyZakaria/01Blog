import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { UserService, UserDTO } from '../../services/user.service';
import { PostService, PostDTO } from '../../services/post.service';
import { SubscriptionService } from '../../services/subscription.service';
import { PostCardComponent } from '../../shared/post-card/post-card.component';
import { ReportModalComponent } from '../../shared/report-modal/report-modal.component';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, PostCardComponent, ReportModalComponent],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    user: UserDTO | null = null;
    posts: PostDTO[] = [];
    loading: boolean = false;
    isOwnProfile: boolean = false;
    showReportModal: boolean = false;
    isEditMode: boolean = false;
    editForm = {
        name: '',
        email: ''
    };
    saving: boolean = false;
    editError: string = '';
    successMessage: string = '';
    private successTimer: ReturnType<typeof setTimeout> | null = null;

    constructor(
        private route: ActivatedRoute,
        private userService: UserService,
        private postService: PostService,
        private subscriptionService: SubscriptionService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.route.params.subscribe(params => {
            const userId = params['id'];
            this.loadProfile(userId);
            this.loadUserPosts(userId);
            this.checkIfOwnProfile(userId);
        });
    }

    loadProfile(userId: string) {
        this.loading = true;
        this.userService.getUserById(userId).subscribe({
            next: (user) => {
                this.user = user;
                this.loading = false;
                this.cdr.detectChanges(); // Force view update
            },
            error: (error) => {
                console.error('Error loading profile:', error);
                this.loading = false;
                this.cdr.detectChanges();
            }
        });
    }

    loadUserPosts(userId: string) {
        this.postService.getUserPosts(userId).subscribe({
            next: (posts) => {
                this.posts = posts;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error loading user posts:', error);
                this.cdr.detectChanges();
            }
        });
    }

    checkIfOwnProfile(userId: string) {
        this.userService.getCurrentUserObservable().subscribe({
            next: (currentUser) => {
                this.isOwnProfile = currentUser.id === userId;
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error checking own profile:', error);
                this.isOwnProfile = false;
            }
        });
    }

    toggleSubscription() {
        if (!this.user) return;

        this.subscriptionService.toggleFollow(this.user.id).subscribe({
            next: (response: { following: boolean, followersCount: number }) => {
                if (this.user) {
                    this.user.isFollowedByCurrentUser = response.following;
                    this.user.followersCount = response.followersCount;
                    this.cdr.detectChanges();
                }
            },
            error: (error: any) => {
                console.error('Error toggling subscription:', error);
            }
        });
    }

    openReportModal() {
        this.showReportModal = true;
    }

    closeReportModal() {
        this.showReportModal = false;
    }

    onReported() {
        alert('Report submitted successfully');
    }

    toggleEditMode() {
        this.isEditMode = !this.isEditMode;
        if (this.isEditMode && this.user) {
            // Populate form with current values
            this.editForm.name = this.user.name;
            this.editForm.email = this.user.email;
            this.editError = '';
        }
    }

    saveProfile() {
        if (!this.editForm.name.trim() || !this.editForm.email.trim()) {
            this.editError = 'Name and email are required';
            return;
        }

        // Basic email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(this.editForm.email)) {
            this.editError = 'Please enter a valid email address';
            return;
        }

        this.saving = true;
        this.editError = '';
        this.successMessage = '';

        this.userService.updateProfile({
            name: this.editForm.name,
            email: this.editForm.email
        }).subscribe({
            next: (updatedUser) => {
                this.user = updatedUser;
                this.isEditMode = false;
                this.saving = false;
                this.showSuccess('Profile updated successfully');
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error updating profile:', error);
                this.editError = error.error?.message || 'Failed to update profile';
                this.saving = false;
                this.cdr.detectChanges();
            }
        });
    }

    private showSuccess(message: string) {
        this.successMessage = message;
        if (this.successTimer) {
            clearTimeout(this.successTimer);
        }
        this.successTimer = setTimeout(() => {
            this.successMessage = '';
            this.cdr.detectChanges();
        }, 3000);
    }

    cancelEdit() {
        this.isEditMode = false;
        this.editError = '';
    }

    onLike(postId: string) {
        this.postService.toggleLike(postId).subscribe({
            next: (response) => {
                const post = this.posts.find(p => p.id === postId);
                if (post) {
                    post.likedByCurrentUser = response.liked;
                    post.likeCount = response.likeCount;
                    this.cdr.detectChanges();
                }
            },
            error: (error) => {
                console.error('Error toggling like:', error);
            }
        });
    }

    onDelete(postId: string) {
        if (!confirm('Are you sure you want to delete this post?')) return;

        this.postService.deletePost(postId).subscribe({
            next: () => {
                this.posts = this.posts.filter(p => p.id !== postId);
                this.cdr.detectChanges();
            },
            error: (error) => {
                console.error('Error deleting post:', error);
            }
        });
    }

    getUserInitials(): string {
        if (!this.user?.name) return '?';
        const names = this.user.name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }
}
