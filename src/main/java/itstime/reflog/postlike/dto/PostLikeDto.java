package itstime.reflog.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PostLikeDto {

    @Getter
    @AllArgsConstructor
    public static class PostLikeSaveRequest{
        private String postType;
        private String likeType;
    }
}
