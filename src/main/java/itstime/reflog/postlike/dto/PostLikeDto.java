package itstime.reflog.postlike.dto;

import itstime.reflog.community.domain.Community;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.retrospect.domain.Retrospect;
import lombok.*;

public class PostLikeDto {

    @Getter
    @AllArgsConstructor
    public static class PostLikeSaveRequest{
        private String postType;
        private String likeType;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BookMarkResponse{
        private String title;
        private String content;
        PostType postType;
        private Long postId;
        private int progressLevel;

        public static BookMarkResponse fromCommunityEntity(Community community){
            return BookMarkResponse.builder()
                    .title(community.getTitle())
                    .content(community.getContent())
                    .postId(community.getId())
                    .postType(PostType.COMMUNITY)
                    .build();
        }
        public static BookMarkResponse fromRetrospectEntity(Retrospect retrospect){
            return BookMarkResponse.builder()
                    .title(retrospect.getTitle())
                    .postId(retrospect.getId())
                    .progressLevel(retrospect.getProgressLevel())
                    .postType(PostType.RETROSPECT)
                    .build();
        }
    }
}
