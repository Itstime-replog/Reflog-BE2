package itstime.reflog.mypage.dto;

import itstime.reflog.community.dto.CommunityDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
    }

    @Getter
    @AllArgsConstructor
    public static class MyPageProfileResponse {
        private String nickname;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class MyPagePostResponse {
        private List<CommunityDto.MyPageCommunityResponse> myPageCommunityResponseList;
    }
}
