import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService, PostDTO } from '../../services/post.service';
import { PostCardComponent } from '../../shared/post-card/post-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, PostCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  posts: PostDTO[] = [];
  loading: boolean = false;
  error: string = '';

  constructor(private postService: PostService) { }

  ngOnInit() {
    this.loadFeed();
  }

  loadFeed() {
    this.loading = true;
    this.error = '';

    this.postService.getFeed().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading feed:', error);
        this.error = 'Failed to load feed';
        this.loading = false;
      }
    });
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
}
