package itstime.reflog.oauth.token.repository;

import itstime.reflog.oauth.token.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository{

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final RedisTemplate redisTemplate;

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(
                refreshToken.getMemberId().toString(),
                refreshToken.getRefreshToken()
        );
        redisTemplate.expire(
                refreshToken.getMemberId().toString(),
                REFRESH_TOKEN_EXPIRATION_TIME,
                TimeUnit.MILLISECONDS
        );
    }


    public Optional<RefreshToken> findByMemberId(final UUID memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(memberId.toString());

        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(memberId, refreshToken));
    }
}
