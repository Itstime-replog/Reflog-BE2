package itstime.reflog.retrospect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import itstime.reflog.retrospect.domain.Bad;

public interface BadRepository extends JpaRepository<Bad, Long> {
}
