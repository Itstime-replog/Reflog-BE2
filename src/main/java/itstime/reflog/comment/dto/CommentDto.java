package itstime.reflog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    public static class CommentSaveOrUpdateRequest {

        @NotBlank(message = "content는 비어 있을 수 없습니다.")
        private String content;

        private Long parentId;
    }

    @Getter
    @AllArgsConstructor
    public static class CommentResponse {

        private String content;
        private LocalDateTime createdAt;
    }
}
