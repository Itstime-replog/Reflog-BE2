package itstime.reflog.member.domain;

import java.util.List;

import itstime.reflog.community.domain.Community;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.schedule.domain.Schedule;
import itstime.reflog.todolist.domain.Todolist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private UUID memberId;

	private String providerId;

	private String provider;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	private String profileImageUrl;

	private String email;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Todolist> todolists;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<DailyGoal> dailyGoals;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<DailyGoal> retrospects;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Schedule> schedules;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Community> communities;

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
	private MyPage myPage;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, length = 20)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", length = 20)
	private LocalDateTime updatedAt;
}
