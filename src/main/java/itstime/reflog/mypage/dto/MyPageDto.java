package itstime.reflog.mypage.dto;

import itstime.reflog.community.domain.Community;
import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserBadge;
import itstime.reflog.retrospect.domain.Retrospect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MyPageDto {

    @Getter
    @AllArgsConstructor
    public static class MyPageInfoResponse {
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class MyPageProfileRequest {
        @Size(min = 2, max = 20, message = "이름은 2~20글자로 한글, 영문, 숫자, 특수기호(,)(-)만 사용할 수 있습니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9.-]+$", message = "이름은 2~20글자로 한글, 영문, 숫자, 특수기호(,)(-)만 사용할 수 있습니다.")
        private String nickname;

        @Email(message = "이메일 정보가 올바르지 않습니다.")
        private String email;

        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class MyPageProfileResponse {
        private String nickname;
        private String email;
        private String imageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyPagePostResponse {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private List<String> postTypes;
        private List<String> learningTypes;
        private Integer progressLevel;    // Retrospect 전용
        private Integer understandingLevel;// Retrospect 전용

        private int totalLike;
        private long commentCount;

        public static MyPagePostResponse fromCommunity(Community community, List<String> postTypes,List<String> learningTypes, int totalLike, long commentCount){
            return MyPagePostResponse.builder()
                    .id(community.getId())
                    .title(community.getTitle())
                    .content(community.getContent())
                    .createdAt(community.getCreatedAt())
                    .postTypes(community.getPostTypes())
                    .learningTypes(community.getLearningTypes())
                    .totalLike(totalLike)
                    .commentCount(commentCount)
                    .build();
        }
        public static MyPagePostResponse fromRetrospect(Retrospect retrospect, int totalLike, long commentCount){
            return MyPagePostResponse.builder()
                    .id(retrospect.getId())
                    .title(retrospect.getTitle())
                    .createdAt(retrospect.getCreatedDate().atStartOfDay())
                    .postTypes(List.of("회고일지"))
                    .learningTypes(retrospect.getStudyTypes().stream()
                            .map(studyType -> studyType.getType())
                            .collect(Collectors.toList()))
                    .progressLevel(retrospect.getProgressLevel())
                    .understandingLevel(retrospect.getUnderstandingLevel())
                    .totalLike(totalLike)
                    .commentCount(commentCount)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyPageBadgeResponse {
        private Badge badgeName;
        private boolean isEarned;

        public static MyPageBadgeResponse fromBadge(UserBadge userBadge) {
            return MyPageBadgeResponse.builder()
                    .badgeName(userBadge.getBadge())
                    .isEarned(userBadge.isEarned())
                    .build();
        }
    }
}
