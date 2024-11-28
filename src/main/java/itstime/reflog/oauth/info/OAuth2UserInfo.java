package itstime.reflog.oauth.info;

import java.util.Map;

public interface OAuth2UserInfo {
	String getProviderId();
	String getProvider();
	String getName();
	String getProfileImageUrl();
}
