package itstime.reflog.retrospect.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.retrospect.domain.Good;

public interface GoodRepository extends JpaRepository<Good, Long> {
}
