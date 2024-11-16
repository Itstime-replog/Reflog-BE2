package itstime.reflog.oauth.info.impl;

import itstime.reflog.oauth.info.OAuth2UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    //    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
    //        super(attributes);
    //    }
    private static final Logger log = LoggerFactory.getLogger(NaverOAuth2UserInfo.class);
    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // 디버깅용 로그 추가
        log.debug("Naver API Response: {}", attributes);
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null || response.get("id") == null) {
            throw new IllegalArgumentException("Missing provider ID in response");
        }
        return response.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("profile_image");
    }
}
