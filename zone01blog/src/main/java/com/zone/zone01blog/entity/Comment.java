package com.zone.zone01blog.entity;

import lombok.Data;

@Data
public class Comment {
    private String id;
    private String comment;
    private String postId;
    private String userId;

    public Comment(String id, String comment, String postId, String userId){
        this.id = id;
        this.comment = comment;
        this.postId = postId;
        this.userId = userId;
    }
}
