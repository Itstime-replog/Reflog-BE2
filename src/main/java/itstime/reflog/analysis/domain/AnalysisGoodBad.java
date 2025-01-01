package itstime.reflog.analysis.domain;

import itstime.reflog.analysis.domain.enums.GoodBad;
import itstime.reflog.analysis.domain.enums.Period;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class AnalysisGoodBad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_goodbad_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int percentage;

    @Enumerated(EnumType.STRING)
    private GoodBad goodBad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_analysis_id", nullable = false)
    private WeeklyAnalysis weeklyAnalysis;
}
