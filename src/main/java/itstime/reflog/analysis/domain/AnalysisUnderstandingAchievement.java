package itstime.reflog.analysis.domain;

import itstime.reflog.analysis.domain.enums.Period;
import itstime.reflog.analysis.domain.enums.UnderstandingAchievement;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class AnalysisUnderstandingAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_understanding_achievement_id")
    private Long id;

    @Column(nullable = false)
    private String day;

    @Column(nullable = false)
    private int percentage;

    @Enumerated(EnumType.STRING)
    private UnderstandingAchievement understandingAchievement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_analysis_id", nullable = false)
    private WeeklyAnalysis weeklyAnalysis;
}
