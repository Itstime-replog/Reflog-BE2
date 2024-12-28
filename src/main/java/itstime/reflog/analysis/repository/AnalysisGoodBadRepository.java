package itstime.reflog.analysis.repository;

import itstime.reflog.analysis.domain.AnalysisGoodBad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisGoodBadRepository extends JpaRepository<AnalysisGoodBad, Long> {
}
