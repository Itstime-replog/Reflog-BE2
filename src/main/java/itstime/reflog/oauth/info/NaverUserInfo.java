package itstime.reflog.oauth.info;

import itstime.reflog.member.domain.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private static final Logger log = LoggerFactory.getLogger(NaverUserInfo.class);
    private final Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // 디버깅용 로그 추가
        log.debug("Naver API Response: {}", attributes);
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        log.info("Attributes: {}", attributes);

        return (String) attributes.get("nickname");
    }

    @Override
    public String getProvider() {
        return Provider.NAVER_PROVIDER.getProvider();
    }

    @Override
    public String getProfileImageUrl() {

        return (String) attributes.get("profile_image");
    }
}
