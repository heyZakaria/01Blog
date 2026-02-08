import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommentService, CommentDTO, CreateCommentRequest } from '../../services/comment.service';

@Component({
    selector: 'app-comment-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './comment-list.component.html',
    styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent implements OnInit {
    @Input() postId!: string;
    comments: CommentDTO[] = [];
    newComment: string = '';
    loading: boolean = false;
    editingCommentId: string | null = null;
    editContent: string = '';

    constructor(private commentService: CommentService) { }

    ngOnInit() {
        this.loadComments();
    }

    loadComments() {
        this.loading = true;
        this.commentService.getPostComments(this.postId).subscribe({
            next: (comments) => {
                this.comments = comments;
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading comments:', error);
                this.loading = false;
            }
        });
    }

    onKeyDown(event: Event) {
        const keyboardEvent = event as KeyboardEvent;
        if (keyboardEvent.ctrlKey) {
            this.addComment();
        }
    }

    addComment() {
        if (!this.newComment.trim()) return;

        const request: CreateCommentRequest = {
            content: this.newComment
        };

        this.commentService.createComment(this.postId, request).subscribe({
            next: (comment) => {
                this.comments.unshift(comment);
                this.newComment = '';
            },
            error: (error) => {
                console.error('Error creating comment:', error);
            }
        });
    }

    startEdit(comment: CommentDTO) {
        this.editingCommentId = comment.id;
        this.editContent = comment.content;
    }

    cancelEdit() {
        this.editingCommentId = null;
        this.editContent = '';
    }

    saveEdit(commentId: string) {
        if (!this.editContent.trim()) return;

        this.commentService.updateComment(commentId, { content: this.editContent }).subscribe({
            next: (updatedComment) => {
                const index = this.comments.findIndex(c => c.id === commentId);
                if (index !== -1) {
                    this.comments[index] = updatedComment;
                }
                this.cancelEdit();
            },
            error: (error) => {
                console.error('Error updating comment:', error);
            }
        });
    }

    deleteComment(commentId: string) {
        if (!confirm('Delete this comment?')) return;

        this.commentService.deleteComment(commentId).subscribe({
            next: () => {
                this.comments = this.comments.filter(c => c.id !== commentId);
            },
            error: (error) => {
                console.error('Error deleting comment:', error);
            }
        });
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
