package itstime.reflog.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.domain.Provider;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByProviderIdAndProvider(String providerId, Provider provider);

	Member findByProviderId(String providerId);
}
