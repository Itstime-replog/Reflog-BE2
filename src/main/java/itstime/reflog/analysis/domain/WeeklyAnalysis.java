package itstime.reflog.analysis.domain;

import itstime.reflog.analysis.domain.enums.Period;
import itstime.reflog.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeeklyAnalysis {
    @Id
    @Column(name = "weekly_analysis_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int todos;

    @Column(nullable = false)
    private int completedTodos;

    @Column(nullable = false)
    private int retrospects;

    @Column(nullable = false)
    private long totalStudyType;

    @OneToMany(mappedBy = "weeklyAnalysis", cascade = CascadeType.ALL)
    List<Improvement> improvements;

    @OneToMany(mappedBy = "weeklyAnalysis", cascade = CascadeType.ALL)
    List<AnalysisGoodBad> analysisGoodsBads;

    @OneToMany(mappedBy = "weeklyAnalysis", cascade = CascadeType.ALL)
    List<AnalysisUnderstandingAchievement> analysisUnderstandingAchievements;

    @OneToMany(mappedBy = "weeklyAnalysis", cascade = CascadeType.ALL)
    List<AnalysisStudyType> studyTypes;

    @Enumerated(EnumType.STRING)
    private Period period;

}
