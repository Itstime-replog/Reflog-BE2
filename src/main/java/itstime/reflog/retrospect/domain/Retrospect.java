package itstime.reflog.retrospect.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.member.domain.Member;
import itstime.reflog.postlike.domain.PostLike;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Retrospect {
	@Id
	@Column(name = "retrospect_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(name = "created_date")
	private LocalDate createdDate;

	@Column(nullable = false)
	private int progressLevel; // 수행 정도

	@Column(nullable = false)
	private int understandingLevel; // 이해도

	@Column(nullable = false)
	private String actionPlan;

	@Column(nullable = false)
	private boolean visibility;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "retrospect", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StudyType> studyTypes;

	@OneToMany(mappedBy = "retrospect", cascade = CascadeType.ALL)
	private List<Good> goods;

	@OneToMany(mappedBy = "retrospect", cascade = CascadeType.ALL)
	private List<Bad> bads;

	@OneToMany(mappedBy = "retrospect", cascade = CascadeType.ALL)
	private List<PostLike> postLikes;

	@OneToMany(mappedBy = "retrospect", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<Comment> comments = new ArrayList<>();

	public void updateStudyTypes(List<StudyType> newStudyTypes) {
		this.studyTypes.clear();
		this.studyTypes.addAll(newStudyTypes);
	}

	public void updateGoods(List<Good> newGoods) {
		this.goods.clear();
		this.goods.addAll(newGoods);
	}

	public void updateBads(List<Bad> newBads) {
		this.bads.clear();
		this.bads.addAll(newBads);
	}

}
