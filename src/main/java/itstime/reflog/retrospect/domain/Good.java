package itstime.reflog.retrospect.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Good {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "good_id")
	private Long id;

	@Column(nullable = false)
	private String content; // 잘한 점

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "retrospect_id", nullable = false)
	private Retrospect retrospect;
}
