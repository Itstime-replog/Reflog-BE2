package itstime.reflog.oauth.info;

import itstime.reflog.member.domain.Provider;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

	private Map<String, Object> attributes;

	@Override
	public String getProviderId() {
		return attributes.get("id").toString();
	}

	@Override
	public String getName() {
		Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

		if (properties == null) {
			return null;
		}

		return (String) properties.get("nickname");
	}

	@Override
	public String getProvider() {
		return Provider.KAKAO_PROVIDER.getProvider();
	}

	@Override
	public String getProfileImageUrl(){
		Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
		if (properties == null) {
			return null;
		}
		return (String) properties.get("profile_image");
	}
}
