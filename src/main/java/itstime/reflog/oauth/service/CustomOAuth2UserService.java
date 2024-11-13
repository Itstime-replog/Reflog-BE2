package itstime.reflog.oauth.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.domain.ProviderType;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.oauth.domain.UserPrincipal;
import itstime.reflog.oauth.info.OAuth2UserInfo;
import itstime.reflog.oauth.info.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

	private final MemberRepository memberRepository;
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 소셜 로그인 식별 필드
		String providerTypeKey = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
		ProviderType providerType = ProviderType.valueOf(providerTypeKey);

		// 전달받은 사용자 속성
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// OAuth2UserInfo를 통해 특정 Provider에 맞는 사용자 정보를 가져옴
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, attributes);

		// 사용자 정보 조회 시 providerId 사용
		String providerId = oAuth2UserInfo.getProviderId();

		// 사용자 정보를 확인하고, 없는 경우 새로 저장
		Member member = getOrCreateMember(providerId,providerType, oAuth2UserInfo);

		// 권한 설정 (기본 권한 USER로 설정)
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

		// Security Context에 저장할 사용자 정보 생성
		return new UserPrincipal(member, attributes, authorities);
	}

	// DB에서 사용자 정보를 조회하거나 없는 경우 새로 생성
	private Member getOrCreateMember(String providerId, ProviderType providerType, OAuth2UserInfo oAuth2UserInfo) {
		Optional<Member> memberOptional = memberRepository.findByProviderIdAndProviderType(providerId, providerType);

		return memberOptional.orElseGet(() -> {
			Member newMember = Member.builder()
				.name(oAuth2UserInfo.getName())
				.providerId(providerId)
				.profileImageUrl(oAuth2UserInfo.getProfileImageUrl())
				.providerType(providerType)
				.build();
			return memberRepository.save(newMember);
		});
	}
}
