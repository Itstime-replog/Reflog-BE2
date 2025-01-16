package itstime.reflog.community.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.member.domain.Member;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.postlike.domain.PostLike;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@BatchSize(size = 50)
public class Community {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title; // 제목

	private String content; // 게시글 내용

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "community_post_types", joinColumns = @JoinColumn(name = "community_id"))
	@Column(name = "post_type")
	private List<String> postTypes; // 글 유형 (최대 2개)

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "community_learning_types", joinColumns = @JoinColumn(name = "community_id"))
	@Column(name = "learning_type")
	private List<String> learningTypes; // 학습 유형 (최대 2개)

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UploadedFile> uploadedFiles; // 업로드된 파일들 (PDF 및 이미지)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
	private List<PostLike> postLikes;

	@OneToMany(mappedBy = "community", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Comment> comments = new ArrayList<>();

	private LocalDateTime createdAt; // 생성일
	private LocalDateTime updatedAt; // 수정일

	public void update(String title, String content, List<String> postTypes, List<String> learningTypes) {
		this.title = title;
		this.content = content;
		this.postTypes = postTypes;
		this.learningTypes = learningTypes;
		this.updatedAt = LocalDateTime.now();
	}
}
