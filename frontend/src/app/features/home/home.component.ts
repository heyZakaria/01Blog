import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService, PostDTO } from '../../services/post.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  posts: PostDTO[] = [];
  isLoading = true;

  constructor(private postService: PostService) { }

  ngOnInit() {
    this.postService.getFeed().subscribe({
      next: (data) => {
        this.posts = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to fetch feed', err);
        this.isLoading = false;
      }
    });
  }
}
