package itstime.reflog.oauth.token.repository;

import itstime.reflog.oauth.token.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository{

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;
    @Qualifier("redisTemplate")
    private final RedisTemplate redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    // Refresh Token 저장
    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = generateKey(refreshToken.getMemberId());
        valueOperations.set(key, refreshToken.getRefreshToken());

        // TTL 설정
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION_TIME, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public Optional<RefreshToken> findByMemberId(final UUID memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key = generateKey(memberId);
        String refreshToken = valueOperations.get(key);

        return Optional.ofNullable(refreshToken)
                .map(token -> new RefreshToken(memberId, token));
    }

    private String generateKey(UUID memberId) {
        return REFRESH_TOKEN_PREFIX + memberId.toString();
    }
}
