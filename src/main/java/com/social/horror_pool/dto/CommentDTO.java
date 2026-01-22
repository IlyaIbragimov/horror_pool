package com.social.horror_pool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long commentId;

    @NotBlank(message = "Comment content cannot be blank")
    private String commentContent;

    private String userName;

    private String date;

}
