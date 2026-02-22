// Purpose: Home feed page component.
import { Component, OnInit, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService, PostDTO } from '../../services/post.service';
import { PostCardComponent } from '../../shared/post-card/post-card.component';

@Component({
  selector: 'app-home',
  imports: [CommonModule, PostCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
// Class: Component logic.
export class HomeComponent implements OnInit {
  readonly posts = signal<PostDTO[]>([]);
  // State: reactive value for the template.
  readonly loading = signal(false);
  // State: reactive value for the template.
  readonly error = signal('');
  readonly hasPosts = computed(() => this.posts().length > 0);

  // Constructor: injects dependencies.
  constructor(private postService: PostService) { }

  // Angular lifecycle: ng on init.
  ngOnInit() {
    this.loadFeed();
  }

  // Loads  feed.
  loadFeed() {
    this.loading.set(true);
    this.error.set('');

    this.postService.getFeed().subscribe({
      next: (posts) => {
        this.posts.set(posts);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading feed:', error);
        this.error.set('Failed to load feed');
        this.loading.set(false);
      }
    });
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
  onDelete(postId: string) {
    this.postService.deletePost(postId).subscribe({
      next: () => {
        this.posts.update((posts) => posts.filter(p => p.id !== postId));
      },
      error: (error) => {
        console.error('Error deleting post:', error);
      }
    });
  }
}
