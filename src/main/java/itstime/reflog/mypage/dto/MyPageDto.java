package itstime.reflog.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MyPageDto {

    @Getter
    @AllArgsConstructor
    public static class MyPageInfoResponse {
        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public static class MyPageMissionResponse {

    }
}
