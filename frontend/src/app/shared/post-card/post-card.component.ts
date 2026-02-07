import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostDTO } from '../../services/post.service';

@Component({
    selector: 'app-post-card',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './post-card.component.html',
    styleUrls: ['./post-card.component.css']
})
export class PostCardComponent {
    @Input() post!: PostDTO;
    @Input() showActions: boolean = true;
    @Output() like = new EventEmitter<string>();
    @Output() delete = new EventEmitter<string>();

    onLike() {
        this.like.emit(this.post.id);
    }

    onDelete() {
        if (confirm('Are you sure you want to delete this post?')) {
            this.delete.emit(this.post.id);
        }
    }

    getAuthorInitials(): string {
        if (!this.post.author?.name) return '?';
        const names = this.post.author.name.split(' ');
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
            return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
        }
    }
}
