package itstime.reflog.oauth.token.service;

import itstime.reflog.oauth.token.domain.RefreshToken;
import itstime.reflog.oauth.token.dto.TokenDto;
import itstime.reflog.oauth.token.exception.TokenErrorResult;
import itstime.reflog.oauth.token.exception.TokenException;
import itstime.reflog.oauth.token.repository.RefreshTokenRepository;
import itstime.reflog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public TokenDto.TokenResponse reissueAccessToken(String authorizationHeader) {
        String refreshToken = jwtUtil.getTokenFromHeader(authorizationHeader);
        String memberId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByMemberId(UUID.fromString(memberId))
                .orElseThrow(() -> new TokenException(TokenErrorResult.REFRESH_TOKEN_NOT_FOUND));
        String accessToken = null;

        if (!existRefreshToken.getRefreshToken().equals(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            // 리프레쉬 토큰이 다르거나, 만료된 경우
            throw new TokenException(TokenErrorResult.INVALID_REFRESH_TOKEN); // 401 에러를 던져 재로그인을 요청
        } else {
            // 액세스 토큰 재발급
            accessToken = jwtUtil.generateAccessToken(UUID.fromString(memberId), ACCESS_TOKEN_EXPIRATION_TIME);
        }

        return TokenDto.TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
