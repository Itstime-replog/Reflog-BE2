package itstime.reflog.oauth.token.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@RedisHash("token")
public class RefreshToken {
    @Id
    private Long id;

    private UUID memberId;

    private String refreshToken;

    public RefreshToken(UUID memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
