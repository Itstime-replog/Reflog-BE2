package itstime.reflog.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.Community;

public interface CommunityRepository extends JpaRepository<Community, Long> {
}
