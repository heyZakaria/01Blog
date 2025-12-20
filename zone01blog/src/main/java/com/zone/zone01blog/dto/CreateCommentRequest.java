package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String comment;

    public CreateCommentRequest(){};
    public CreateCommentRequest(String comment){
        this.comment = comment;
    }
}
