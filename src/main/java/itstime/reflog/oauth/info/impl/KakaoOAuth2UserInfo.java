package itstime.reflog.oauth.info.impl;

import java.util.Map;

import itstime.reflog.oauth.info.OAuth2UserInfo;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

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
	public String getProfileImageUrl(){
		Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
		if (properties == null) {
			return null;
		}
		return (String) properties.get("profile_image");
	}
}
