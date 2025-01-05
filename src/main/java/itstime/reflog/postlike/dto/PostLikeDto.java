package itstime.reflog.postlike.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class PostLikeDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PostLikeSaveOrUpdateRequest{
        private Boolean isLike;
    }

}
