// Purpose: Comment list component.
import { Component, OnInit, ChangeDetectionStrategy, signal, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { CommentService, CommentDTO, CreateCommentRequest } from '../../services/comment.service';
import { DialogService } from '../../core/services/dialog.service';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-comment-list',
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './comment-list.component.html',
    styleUrls: ['./comment-list.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class CommentListComponent implements OnInit {
    readonly postId = input.required<string>();
    readonly commentCountChange = output<number>();
    readonly comments = signal<CommentDTO[]>([]);
    // State: reactive value for the template.
    readonly loading = signal(false);
    readonly editingCommentId = signal<string | null>(null);
    readonly commentCount = computed(() => this.comments().length);
    // Form control: single input state.
    readonly newCommentControl = new FormControl('', { nonNullable: true });
    // Form control: single input state.
    readonly editContentControl = new FormControl('', { nonNullable: true });

    // Checks if post.
    get canPost(): boolean {
        return this.newCommentControl.value.trim().length > 0;
    }

    // Constructor: injects dependencies.
    constructor(
        private commentService: CommentService,
        private dialogService: DialogService,
        private authService: AuthService
    ) { }

    // Angular lifecycle: ng on init.
    ngOnInit() {
        this.loadComments();
    }

    // Loads  comments.
    loadComments() {
        this.loading.set(true);
        this.commentService.getPostComments(this.postId()).subscribe({
            next: (comments) => {
                this.comments.set(comments);
                this.commentCountChange.emit(this.comments().length);
                this.loading.set(false);
            },
            error: (error) => {
                console.error('Error loading comments:', error);
                this.loading.set(false);
            }
        });
    }

    // Handles key down.
    onKeyDown(event: Event) {
        const keyboardEvent = event as KeyboardEvent;
        if (keyboardEvent.ctrlKey) {
            this.addComment();
        }
    }

    // Method: add comment.
    addComment() {
        if (!this.newCommentControl.value.trim()) return;

        const request: CreateCommentRequest = {
            content: this.newCommentControl.value
        };

        this.commentService.createComment(this.postId(), request).subscribe({
            next: (comment) => {
                this.comments.update((comments) => [comment, ...comments]);
                this.commentCountChange.emit(this.comments().length);
                this.newCommentControl.setValue('');
            },
            error: (error) => {
                console.error('Error creating comment:', error);
            }
        });
    }

    // Starts edit.
    startEdit(comment: CommentDTO) {
        if (!this.canModify(comment)) return;
        this.editingCommentId.set(comment.id);
        this.editContentControl.setValue(comment.content);
    }

    // Checks if cancel edit.
    cancelEdit() {
        this.editingCommentId.set(null);
        this.editContentControl.setValue('');
    }

    // Saves edit.
    saveEdit(commentId: string) {
        const target = this.comments().find(c => c.id === commentId);
        if (!target || !this.canModify(target)) return;
        if (!this.editContentControl.value.trim()) return;

        this.commentService.updateComment(this.postId(), commentId, { content: this.editContentControl.value }).subscribe({
            next: (updatedComment) => {
                this.comments.update((comments) => {
                    const next = [...comments];
                    const index = next.findIndex(c => c.id === commentId);
                    if (index !== -1) {
                        next[index] = updatedComment;
                    }
                    return next;
                });
                this.cancelEdit();
            },
            error: (error) => {
                console.error('Error updating comment:', error);
            }
        });
    }

    // Deletes comment.
    async deleteComment(commentId: string) {
        const target = this.comments().find(c => c.id === commentId);
        if (!target || !this.canModify(target)) return;
        const confirmed = await this.dialogService.confirm(
            'Delete Comment',
            'Delete this comment?',
            'Delete'
        );
        if (!confirmed) return;

        this.commentService.deleteComment(this.postId(), commentId).subscribe({
            next: () => {
                this.comments.update((comments) => comments.filter(c => c.id !== commentId));
                this.commentCountChange.emit(this.comments().length);
            },
            error: (error) => {
                console.error('Error deleting comment:', error);
            }
        });
    }

    canModify(comment: CommentDTO): boolean {
        const currentUser = this.authService.getCurrentUser();
        if (!currentUser?.id || !comment.author?.id) return false;
        return currentUser.id === comment.author.id;
    }

    getAuthorInitials(name: string): string {
        if (!name) return '?';
        const names = name.split(' ');
        if (names.length >= 2) {
            return names[0][0] + names[1][0];
        }
        return names[0][0];
    }

    formatDate(dateString: string): string {
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
            return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        }
    }
}
