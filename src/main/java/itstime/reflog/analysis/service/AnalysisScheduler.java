package itstime.reflog.analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisScheduler {

    private final AnalysisService analysisService;

    // 매주 월요일 자정 실행
    @Scheduled(cron = "0 0 0 * * MON")
    public void scheduleWeeklyAnalysis() {
        analysisService.runWeeklyAnalysis();
    }
}
