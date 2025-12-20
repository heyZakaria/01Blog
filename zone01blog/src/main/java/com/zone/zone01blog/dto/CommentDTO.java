package com.zone.zone01blog.dto;

import lombok.Getter;

@Getter
public class CommentDTO {
    private String id;
    private String comment;
    private String postId;

    public CommentDTO(String id, String comment, String postId){
        this.id = id;
        this.comment = comment;
        this.postId = postId;
    }

}
