package itstime.reflog.member.domain;

import lombok.Getter;

public enum Provider {
	KAKAO_PROVIDER("kakao"),
	NAVER_PROVIDER("naver");

	private final String provider;

	Provider(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}
}
