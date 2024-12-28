package itstime.reflog.analysis.repository;

import itstime.reflog.analysis.domain.Improvement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImprovementRepository extends JpaRepository<Improvement, Long> {
}
