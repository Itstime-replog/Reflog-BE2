package itstime.reflog.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
