package com.social.horror_pool.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Comment content cannot be blank")
    private String commentContent;
}
