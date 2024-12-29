package itstime.reflog.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.UploadedFile;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
	List<UploadedFile> findByCommunityId(Long communityId);
}
