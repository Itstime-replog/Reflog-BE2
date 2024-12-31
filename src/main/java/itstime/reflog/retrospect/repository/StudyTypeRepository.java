package itstime.reflog.retrospect.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.retrospect.domain.StudyType;

import java.util.List;

public interface StudyTypeRepository extends JpaRepository<StudyType, Long> {

    List<StudyType> findStudyTypeByType(String type);
}
