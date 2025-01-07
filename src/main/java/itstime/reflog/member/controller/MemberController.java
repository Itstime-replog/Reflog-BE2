package itstime.reflog.member.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.member.service.MemberService;
import itstime.reflog.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class MemberController {

	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final MemberService memberService;

	@GetMapping("/logout")
	public ResponseEntity<CommonApiResponse<Void>> logout(@RequestParam("token") String token) {
		// 1. 사용자 ID 추출
		UUID userId = jwtUtil.getUuidFromToken(token);

		// 2. Redis 블랙리스트 및 Refresh Token 삭제
		long remainingTime = jwtUtil.getRemainingTime(token); // 토큰 만료 시간 계산
		redisTemplate.opsForValue().set("blacklist:" + token, "true", remainingTime, TimeUnit.MILLISECONDS);
		redisTemplate.delete("refresh:" + userId);

		// 3. 로그아웃 완료 응답
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<CommonApiResponse<Void>> delete(@RequestParam("token") String token) {
		// 1. Access Token에서 사용자 UUID 추출
		UUID userId = jwtUtil.getUuidFromToken(token);

		// 2. 사용자 데이터 삭제
		memberService.deleteMember(userId);

		// 3. Redis에서 Refresh Token 삭제
		String refreshTokenKey = "refresh:" + userId;
		redisTemplate.delete(refreshTokenKey);

		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

}
