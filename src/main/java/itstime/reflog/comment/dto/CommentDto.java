package itstime.reflog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    public static class CommentSaveOrUpdateRequest {

        @NotBlank(message = "댓글은 비어 있을 수 없습니다.")
        private String content;

        private Long parentId;
    }

    @Getter
    @AllArgsConstructor
    public static class CommentResponse {
        private Long commentId;
        private String name;
        private String content;
        private Long parentId;
        private LocalDateTime createdAt;
    }
}
