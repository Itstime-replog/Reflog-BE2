package itstime.reflog.oauth.token.repository;

import itstime.reflog.oauth.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    @Query("SELECT u FROM RefreshToken u WHERE u.memberId = :memberId")
    RefreshToken findByMemberId(UUID memberId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken u WHERE u.memberId = :memberId")
    void deleteByMemberId(UUID memberId);
}
