package itstime.reflog.member.domain;

import java.util.List;

import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.todolist.domain.Todolist;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String providerId;

	@Enumerated(EnumType.STRING)
	private ProviderType providerType;

	@Column(nullable = false, unique = true)
	private String name;

	private String profileImageUrl;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Todolist> todolists;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<DailyGoal> dailyGoals;

}
