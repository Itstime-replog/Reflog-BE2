package itstime.reflog.retrospect.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.retrospect.domain.Retrospect;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {
}
