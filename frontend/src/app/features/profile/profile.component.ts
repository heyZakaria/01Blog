// Purpose: Profile page component.
import { Component, OnInit, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { UserService, UserDTO } from '../../services/user.service';
import { PostService, PostDTO } from '../../services/post.service';
import { SubscriptionService } from '../../services/subscription.service';
import { PostCardComponent } from '../../shared/post-card/post-card.component';
import { ReportModalComponent } from '../../shared/report-modal/report-modal.component';
import { DialogService } from '../../core/services/dialog.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-profile',
    imports: [CommonModule, ReactiveFormsModule, PostCardComponent, ReportModalComponent],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class ProfileComponent implements OnInit {
    readonly user = signal<UserDTO | null>(null);
    readonly posts = signal<PostDTO[]>([]);
    // State: reactive value for the template.
    readonly loading = signal(false);
    // State: reactive value for the template.
    readonly isOwnProfile = signal(false);
    // State: reactive value for the template.
    readonly isAuthenticated = signal(false);
    // State: reactive value for the template.
    readonly showReportModal = signal(false);
    // State: reactive value for the template.
    readonly isEditMode = signal(false);
    // Form model: groups form controls.
    readonly editForm = new FormGroup({
        name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] })
    });
    // State: reactive value for the template.
    readonly saving = signal(false);
    // State: reactive value for the template.
    readonly editError = signal('');
    // State: reactive value for the template.
    readonly successMessage = signal('');
    readonly hasPosts = computed(() => this.posts().length > 0);
    readonly postsCount = computed(() => this.posts().length);
    private successTimer: ReturnType<typeof setTimeout> | null = null;

    // Constructor: injects dependencies.
    constructor(
        private route: ActivatedRoute,
        private userService: UserService,
        private postService: PostService,
        private subscriptionService: SubscriptionService,
        private dialogService: DialogService,
        private authService: AuthService
    ) { }

    // Angular lifecycle: ng on init.
    ngOnInit() {
        this.isAuthenticated.set(this.authService.isAuthenticated());
        this.route.params.subscribe(params => {
            const userId = params['id'];
            this.loadProfile(userId);
            this.loadUserPosts(userId);
            this.checkIfOwnProfile(userId);
        });
    }

    // Loads  profile.
    loadProfile(userId: string) {
        this.loading.set(true);
        const request$ = this.isAuthenticated()
            ? this.userService.getUserById(userId)
            : this.userService.getPublicUserById(userId);

        request$.subscribe({
            next: (user) => {
                this.user.set(user);
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error loading profile:', error);
                this.loading.set(false);
            }
        });
    }

    // Loads  user posts.
    loadUserPosts(userId: string) {
        const request$ = this.isAuthenticated()
            ? this.postService.getUserPosts(userId)
            : this.postService.getPublicUserPosts(userId);

        request$.subscribe({
            next: (posts) => {
                this.posts.set(posts);
            },
            error: (error) => {
                console.error('Error loading user posts:', error);
            }
        });
    }

    // Method: check if own profile.
    checkIfOwnProfile(userId: string) {
        if (!this.isAuthenticated()) {
            this.isOwnProfile.set(false);
            return;
        }

        this.userService.getCurrentUserObservable().subscribe({
            next: (currentUser) => {
                this.isOwnProfile.set(currentUser.id === userId);
            },
            error: (error) => {
                console.error('Error checking own profile:', error);
                this.isOwnProfile.set(false);
            }
        });
    }

    // Toggles subscription.
    toggleSubscription() {
        if (!this.user()) return;

        this.subscriptionService.toggleFollow(this.user()!.id).subscribe({
            next: (response: { following: boolean, followersCount: number }) => {
                this.user.update((user) =>
                    user
                        ? { ...user, isFollowedByCurrentUser: response.following, followersCount: response.followersCount }
                        : user
                );
            },
            error: (error: any) => {
                console.error('Error toggling subscription:', error);
            }
        });
    }

    // Opens report modal.
    openReportModal() {
        this.showReportModal.set(true);
    }

    // Closes report modal.
    closeReportModal() {
        this.showReportModal.set(false);
    }

    // Handles reported.
    async onReported() {
        await this.dialogService.alert(
            'Report Submitted',
            'Report submitted successfully.'
        );
    }

    // Toggles edit mode.
    toggleEditMode() {
        this.isEditMode.update((value) => !value);
        if (this.isEditMode() && this.user()) {
            // Populate form with current values
            this.editForm.reset({
                name: this.user()!.name,
                email: this.user()!.email
            });
            this.editError.set('');
        }
    }

    // Saves profile.
    saveProfile() {
        if (this.editForm.invalid) {
            this.editForm.markAllAsTouched();
            this.editError.set('Name and email are required');
            return;
        }

        this.saving.set(true);
        this.editError.set('');
        this.successMessage.set('');
        this.editForm.disable();

        const { name, email } = this.editForm.getRawValue();
        this.userService.updateProfile({
            name,
            email
        }).subscribe({
            next: (updatedUser) => {
                this.user.update((existing) => ({
                    ...existing,
                    ...updatedUser,
                    followersCount: updatedUser.followersCount ?? existing?.followersCount ?? 0,
                    followingCount: updatedUser.followingCount ?? existing?.followingCount ?? 0,
                    isFollowedByCurrentUser: updatedUser.isFollowedByCurrentUser ?? existing?.isFollowedByCurrentUser ?? false
                }) as UserDTO);
                this.isEditMode.set(false);
                this.saving.set(false);
                this.showSuccess('Profile updated successfully');
                this.editForm.enable();
            },
            error: (error) => {
                console.error('Error updating profile:', error);
                this.editError.set(error.error?.message || 'Failed to update profile');
                this.saving.set(false);
                this.editForm.enable();
            }
        });
    }

    // Method: show success.
    private showSuccess(message: string) {
        this.successMessage.set(message);
        if (this.successTimer) {
            clearTimeout(this.successTimer);
        }
        this.successTimer = setTimeout(() => {
            this.successMessage.set('');
        }, 3000);
    }

    // Checks if cancel edit.
    cancelEdit() {
        this.isEditMode.set(false);
        this.editError.set('');
    }

    // Handles like.
    onLike(postId: string) {
        this.postService.toggleLike(postId).subscribe({
            next: (response) => {
                this.posts.update((posts) =>
                    posts.map((post) =>
                        post.id === postId
                            ? { ...post, likedByCurrentUser: response.liked, likeCount: response.likeCount }
                            : post
                    )
                );
            },
            error: (error) => {
                console.error('Error toggling like:', error);
            }
        });
    }

    // Handles delete.
    async onDelete(postId: string) {
        const confirmed = await this.dialogService.confirm(
            'Delete Post',
            'Are you sure you want to delete this post?',
            'Delete'
        );
        if (!confirmed) return;

        this.postService.deletePost(postId).subscribe({
            next: () => {
                this.posts.update((posts) => posts.filter(p => p.id !== postId));
            },
            error: (error) => {
                console.error('Error deleting post:', error);
            }
        });
    }

    getUserInitials(): string {
        if (!this.user()?.name) return '?';
        const names = this.user()!.name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }
}
