import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CommentDTO {
    id: string;
    content: string;
    author: {
        id: string;
        name: string;
        email: string;
    };
    postId: string;
    createdAt: string;
    updatedAt: string;
}

export interface CreateCommentRequest {
    content: string;
}

export interface UpdateCommentRequest {
    content: string;
}

@Injectable({
    providedIn: 'root'
})
export class CommentService {
    private apiUrl = `${environment.apiBaseUrl}/api/v1`;

    constructor(private http: HttpClient) { }

    getPostComments(postId: string): Observable<CommentDTO[]> {
        return this.http.get<CommentDTO[]>(`${this.apiUrl}/posts/${postId}/comments`);
    }

    createComment(postId: string, data: CreateCommentRequest): Observable<CommentDTO> {
        return this.http.post<CommentDTO>(`${this.apiUrl}/posts/${postId}/comments`, data);
    }

    updateComment(postId: string, commentId: string, data: UpdateCommentRequest): Observable<CommentDTO> {
        return this.http.put<CommentDTO>(`${this.apiUrl}/posts/${postId}/comments/${commentId}`, data);
    }

    deleteComment(postId: string, commentId: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/posts/${postId}/comments/${commentId}`);
    }
}
