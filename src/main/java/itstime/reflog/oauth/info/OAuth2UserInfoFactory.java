package itstime.reflog.oauth.info;

import java.util.Map;

import itstime.reflog.member.domain.ProviderType;
import itstime.reflog.oauth.info.impl.KakaoOAuth2UserInfo;
import itstime.reflog.oauth.info.impl.NaverOAuth2UserInfo;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case NAVER:
                return new NaverOAuth2UserInfo(attributes);
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
