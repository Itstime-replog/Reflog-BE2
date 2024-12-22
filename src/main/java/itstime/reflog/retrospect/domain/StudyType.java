package itstime.reflog.retrospect.domain;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class StudyType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "study_type_id")
	private Long id;

	@Column(nullable = false)
	private String type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "retrospect_id", nullable = false)
	private Retrospect retrospect;

}
