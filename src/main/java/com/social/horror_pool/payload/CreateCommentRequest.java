package com.social.horror_pool.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Comment content cannot be blank")
    @Size(max=2000, message = "Comment be longer than 2000 characters")
    private String commentContent;
}
