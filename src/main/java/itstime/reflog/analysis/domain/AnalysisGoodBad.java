package itstime.reflog.analysis.domain;

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

    @Column(nullable = false)
    private String type; //good or bad

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_analysis_id", nullable = false)
    private WeeklyAnalysis weeklyAnalysis;
}
