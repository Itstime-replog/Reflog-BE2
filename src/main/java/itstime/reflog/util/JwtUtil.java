package itstime.reflog.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import itstime.reflog.oauth.token.exception.TokenErrorResult;
import itstime.reflog.oauth.token.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("비밀키 디코딩 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("비밀키 디코딩 오류", e);
        }
    }

    // 액세스 토큰을 발급하는 메서드
    public String generateAccessToken(UUID userId, long expirationMillis) {
        log.info("Access Token 생성 시작. 유효기간(ms): {}", expirationMillis);
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMillis);
        log.info("Access Token 만료 시간: {}", expirationDate);

        String token = Jwts.builder()
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(this.getSigningKey())
                .compact();
        log.info("발급된 토큰: {}", token);
        return token;
    }

    // 리프레쉬 토큰을 발급하는 메서드
    public String generateRefreshToken(UUID userId, long expirationMillis) {
        log.info("리프레쉬 토큰이 발행되었습니다.");

        return Jwts.builder()
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(this.getSigningKey())
                .compact();
    }

    // 응답 헤더에서 액세스 토큰을 반환하는 메서드
    public String getTokenFromHeader(String authorizationHeader) {
        System.out.println("Authorization Header: " + authorizationHeader);
        return authorizationHeader.substring(7);
    }

    // 토큰에서 유저 id를 반환하는 메서드
    public String getUserIdFromToken(String token) {
        try {
            String userId = Jwts.parserBuilder()
                    .setSigningKey(this.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class);

            log.info("유저 id를 반환합니다.");
            return userId;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우
            log.error("유효하지 않은 토큰입니다. 에러 메시지: {}", e.getMessage());
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }
    }

    // Jwt 토큰의 유효기간을 확인하는 메서드
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = Jwts.parserBuilder()
                    .setSigningKey(this.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            log.info("토큰 만료 시간: {}", expirationDate);
            return expirationDate.before(new Date());
        } catch (JwtException e) {
            log.error("토큰 검증 중 에러 발생: {}", e.getMessage());
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }
    }

    public UUID getUuidFromToken(String token) {
        try {
            String userId = Jwts.parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);

            log.info("토큰에서 추출된 UUID: {}", userId);
            return UUID.fromString(userId);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰입니다. 에러 메시지: {}", e.getMessage());
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }
    }

    public long getRemainingTime(String token) {
        try {
            Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

            long remainingTime = expirationDate.getTime() - System.currentTimeMillis();
            log.info("토큰의 남은 유효 시간(ms): {}", remainingTime);
            return remainingTime > 0 ? remainingTime : 0;
        } catch (JwtException e) {
            log.error("토큰 만료 시간 확인 중 에러 발생: {}", e.getMessage());
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }
    }
}
