package itstime.reflog.analysis.repository;

import itstime.reflog.analysis.domain.AnalysisStudyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisStudyTypeRepository extends JpaRepository<AnalysisStudyType, Long> {
}
