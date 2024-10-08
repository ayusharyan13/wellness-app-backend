package com.ayush.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatusCode;

import java.util.Set;

@Schema(
        title = "Blog post model",
        description = "Blog post model information"
)
@Data
public class PostDto {

    private long id;
    @Schema(
            title = "Blog post title name"
    )
    @NotEmpty
    @Size(min = 2, message = "post title should have minimum 2 chars")
    private String title;

    @Schema(
            title = "Blog post description"
    )
    @NotEmpty
    @Size(min = 10, message = "post description should have minimum 10 chars")
    private String description;

    @Schema(
            title = "Blog post content details"
    )
    @NotEmpty
    private String content;

    @Schema(
            title = "Blog post comments"
    )
    private Set<CommentDto> comments;

    private Long categoryId;
}
