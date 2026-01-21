import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PostDTO {
    id: string;
    title: string;
    description: string;
    likes: number;
    author: any;
    createdAt: string;
    commentCount: number;
    likedByCurrentUser: boolean;
    mediaUrl?: string;
    mediaType?: string;
}

export interface CreatePostRequest {
    title: string;
    description: string;
}

@Injectable({
    providedIn: 'root'
})
export class PostService {
    private apiUrl = 'http://localhost:8080/api/v1/posts';

    constructor(private http: HttpClient) { }

    createPost(data: CreatePostRequest): Observable<PostDTO> {
        return this.http.post<PostDTO>(this.apiUrl, data);
    }

    getFeed(): Observable<PostDTO[]> {
        return this.http.get<PostDTO[]>(`${this.apiUrl}/feed`);
    }

    getUserPosts(userId: string): Observable<PostDTO[]> {
        return this.http.get<PostDTO[]>(`http://localhost:8080/api/v1/users/${userId}/posts`);
    }
}
