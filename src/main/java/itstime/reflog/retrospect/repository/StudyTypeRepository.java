package itstime.reflog.retrospect.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.retrospect.domain.StudyType;

public interface StudyTypeRepository extends JpaRepository<StudyType, Long> {
}
