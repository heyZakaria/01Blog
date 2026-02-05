import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { UserService, UserDTO } from '../../services/user.service';
import { PostService, PostDTO } from '../../services/post.service';
import { SubscriptionService } from '../../services/subscription.service';
import { PostCardComponent } from '../../shared/post-card/post-card.component';
import { ReportModalComponent } from '../../shared/report-modal/report-modal.component';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, PostCardComponent, ReportModalComponent],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    user: UserDTO | null = null;
    posts: PostDTO[] = [];
    loading: boolean = false;
    isOwnProfile: boolean = false;
    showReportModal: boolean = false;

    constructor(
        private route: ActivatedRoute,
        private userService: UserService,
        private postService: PostService,
        private subscriptionService: SubscriptionService
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
            },
            error: (error) => {
                console.error('Error loading profile:', error);
                this.loading = false;
            }
        });
    }

    loadUserPosts(userId: string) {
        this.postService.getUserPosts(userId).subscribe({
            next: (posts) => {
                this.posts = posts;
            },
            error: (error) => {
                console.error('Error loading posts:', error);
            }
        });
    }

    checkIfOwnProfile(userId: string) {
        this.userService.getCurrentUser().subscribe({
            next: (currentUser) => {
                this.isOwnProfile = currentUser.id === userId;
            },
            error: () => {
                this.isOwnProfile = false;
            }
        });
    }

    toggleSubscription() {
        if (!this.user) return;

        if (this.user.isFollowedByCurrentUser) {
            this.subscriptionService.unsubscribe(this.user.id).subscribe({
                next: () => {
                    if (this.user) {
                        this.user.isFollowedByCurrentUser = false;
                        this.user.followersCount--;
                    }
                },
                error: (error) => {
                    console.error('Error unsubscribing:', error);
                }
            });
        } else {
            this.subscriptionService.subscribe(this.user.id).subscribe({
                next: () => {
                    if (this.user) {
                        this.user.isFollowedByCurrentUser = true;
                        this.user.followersCount++;
                    }
                },
                error: (error) => {
                    console.error('Error subscribing:', error);
                }
            });
        }
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

    onLike(postId: string) {
        this.postService.toggleLike(postId).subscribe({
            next: (response) => {
                const post = this.posts.find(p => p.id === postId);
                if (post) {
                    post.likedByCurrentUser = response.liked;
                    post.likes = response.likeCount;
                }
            },
            error: (error) => {
                console.error('Error toggling like:', error);
            }
        });
    }

    onDelete(postId: string) {
        this.postService.deletePost(postId).subscribe({
            next: () => {
                this.posts = this.posts.filter(p => p.id !== postId);
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
