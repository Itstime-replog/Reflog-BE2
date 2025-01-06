package itstime.reflog.member.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class MemberController {

	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	@GetMapping("/logout")
	public ResponseEntity<CommonApiResponse<Void>> logout(@RequestHeader("Authorization") String accessToken) {
		// 1. Access Token에서 UUID 추출
		String token = jwtUtil.getTokenFromHeader(accessToken);
		UUID userId = jwtUtil.getUuidFromToken(token);

		// 2. Access Token을 Redis 블랙리스트에 등록
		long remainingTime = jwtUtil.getRemainingTime(token); // 토큰 만료 시간 계산
		redisTemplate.opsForValue().set("blacklist:" + token, "true", remainingTime, TimeUnit.MILLISECONDS);

		// 3. Refresh Token 삭제
		String refreshTokenKey = "refresh:" + userId;
		redisTemplate.delete(refreshTokenKey);

		// 로그아웃 완료 응답
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}
}
