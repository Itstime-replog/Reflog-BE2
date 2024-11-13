package itstime.reflog.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.domain.ProviderType;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByProviderIdAndProviderType(String providerId, ProviderType providerType);
}
