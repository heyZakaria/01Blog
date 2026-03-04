// Purpose: Post card component.
import { Component, ChangeDetectionStrategy, signal, input, output, effect } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PostDTO, CreatePostRequest, PostService } from '../../services/post.service';
import { environment } from '../../../environments/environment';
import { CommentListComponent } from '../comment-list/comment-list.component';
import { DialogService } from '../../core/services/dialog.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-post-card',
    imports: [CommonModule, NgOptimizedImage, RouterModule, CommentListComponent, ReactiveFormsModule],
    templateUrl: './post-card.component.html',
    styleUrls: ['./post-card.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class PostCardComponent {
    readonly post = input.required<PostDTO>();
    readonly showActions = input(true);
    readonly like = output<string>();
    readonly delete = output<string>();
    readonly localPost = signal<PostDTO | null>(null);
    // State: reactive value for the template.
    readonly showComments = signal(false);
    // State: reactive value for the template.
    readonly isEditing = signal(false);
    // State: reactive value for the template.
    readonly actionsOpen = signal(false);
    // State: reactive value for the template.
    readonly editError = signal('');
    // State: reactive value for the template.
    readonly savingEdit = signal(false);
    // Form model: groups form controls.
    readonly editForm = new FormGroup({
        title: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        description: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });
    // Checks if save.
    get canSave(): boolean {
        return this.editForm.valid;
    }

    // Constructor: injects dependencies.
    constructor(
        private dialogService: DialogService,
        private authService: AuthService,
        private postService: PostService
    ) {
        effect(() => {
            this.localPost.set(this.post());
        });
    }

    // Handles like.
    onLike() {
        this.like.emit(this.post().id);
    }

    // Handles delete.
    async onDelete() {
        this.actionsOpen.set(false);
        const confirmed = await this.dialogService.confirm(
            'Delete Post',
            'Are you sure you want to delete this post?',
            'Delete'
        );
        if (confirmed) {
            this.delete.emit(this.post().id);
        }
    }

    canDelete(): boolean {
        const post = this.localPost();
        if (!post) return false;
        const currentUser = this.authService.getCurrentUser();
        if (!currentUser || !post.author?.id) return false;
        if (currentUser.role === 'ADMIN') return true;
        return currentUser.id === post.author.id;
    }

    canEdit(): boolean {
        const post = this.localPost();
        if (!post) return false;
        const currentUser = this.authService.getCurrentUser();
        if (!currentUser || !post.author?.id) return false;
        if (currentUser.role === 'ADMIN') return true;
        return currentUser.id === post.author.id;
    }

    // Toggles comments.
    toggleComments() {
        this.showComments.update((open) => !open);
    }

    // Toggles actions menu.
    toggleActionsMenu() {
        this.actionsOpen.update((open) => !open);
    }

    // Closes actions menu.
    closeActionsMenu() {
        this.actionsOpen.set(false);
    }

    // Starts edit.
    startEdit() {
        this.closeActionsMenu();
        this.isEditing.set(true);
        const post = this.localPost();
        this.editForm.reset({
            title: post?.title ?? '',
            description: post?.description ?? ''
        });
        this.editError.set('');
    }

    // Checks if cancel edit.
    cancelEdit() {
        this.isEditing.set(false);
        this.editError.set('');
    }

    // Saves edit.
    saveEdit() {
        if (this.editForm.invalid) {
            this.editForm.markAllAsTouched();
            this.editError.set('Title and description are required');
            return;
        }

        const { title, description } = this.editForm.getRawValue();
        const payload: CreatePostRequest = {
            title,
            description
        };

        this.savingEdit.set(true);
        this.editError.set('');
        this.editForm.disable();

        this.postService.updatePost(this.post().id, payload).subscribe({
            next: (updated) => {
                this.localPost.update((post) =>
                    post
                        ? { ...post, title: updated.title, description: updated.description, updatedAt: updated.updatedAt }
                        : post
                );
                this.isEditing.set(false);
                this.savingEdit.set(false);
                this.editForm.enable();
            },
            error: (error) => {
                this.editError.set(error.error?.message || 'Failed to update post');
                this.savingEdit.set(false);
                this.editForm.enable();
            }
        });
    }

    // Handles comment count change.
    onCommentCountChange(count: number) {
        this.localPost.update((post) => (post ? { ...post, commentCount: count } : post));
    }

    getAuthorInitials(): string {
        const post = this.localPost();
        if (!post?.author?.name) return '?';
        const names = post.author.name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }

    formatDate(dateString: string): string {
        if (!dateString) return '';
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now.getTime() - date.getTime();
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 60) {
            return `${diffMins}m ago`;
        } else if (diffHours < 24) {
            return `${diffHours}h ago`;
        } else if (diffDays < 7) {
            return `${diffDays}d ago`;
        } else {
            return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
        }
    }

    getMediaUrl(mediaUrl?: string): string {
        if (!mediaUrl) {
            return '';
        }

        if (mediaUrl.startsWith('http://') || mediaUrl.startsWith('https://')) {
            return mediaUrl;
        }
        const apiPrefix = '/api/v1';
        const baseUrl = environment.apiBaseUrl;
        const origin = baseUrl.endsWith(apiPrefix) ? baseUrl.slice(0, -apiPrefix.length) : baseUrl;
        if (mediaUrl.startsWith('/')) {
            return `${origin}${mediaUrl}`;
        }
        return `${origin}/${mediaUrl}`;
    }

    isImageMedia(): boolean {
        return this.localPost()?.mediaType?.toLowerCase() === 'image';
    }

    isVideoMedia(): boolean {
        return this.localPost()?.mediaType?.toLowerCase() === 'video';
    }
}
