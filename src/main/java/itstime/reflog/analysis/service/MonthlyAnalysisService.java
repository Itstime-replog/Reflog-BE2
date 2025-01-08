package itstime.reflog.analysis.service;

import itstime.reflog.ai.service.OpenAiService;
import itstime.reflog.analysis.domain.*;
import itstime.reflog.analysis.domain.enums.GoodBad;
import itstime.reflog.analysis.domain.enums.Period;
import itstime.reflog.analysis.domain.enums.UnderstandingAchievement;
import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.analysis.repository.*;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.service.NotificationService;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.todolist.domain.Todolist;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static itstime.reflog.mission.domain.Badge.MONTHLY_REPORTER;

@Service
@AllArgsConstructor
public class MonthlyAnalysisService {

    private final MemberRepository memberRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisGoodBadRepository analysisGoodBadRepository;
    private final AnalysisStudyTypeRepository analysisStudyTypeRepository;
    private final AnalysisUnderstandingAchievementRepository analysisUnderstandingAchievementRepository;
    private final OpenAiService openAiService;
    private final ImprovementRepository improvementRepository;
    private final PeriodFilter periodFilter;
    private final AnalysisCalculator analysisCalculator;
    private final MemberServiceHelper memberServiceHelper;
    private final MissionService missionService;
    private final MyPageRepository myPageRepository;
    private final NotificationService notificationService;


    @Transactional
    public void createMonthlyAnalysis(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        // 현재 연도를 기준으로 특정 월의 첫날과 다음 달 첫날을 계산
        LocalDate thisMonth = today.withDayOfMonth(1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        // 투두, 회고, 기타 계산 수행
        List<Todolist> monthlyTodos = periodFilter.calculateTodo(lastMonth, thisMonth, member);
        List<Retrospect> monthlyRetrospect = periodFilter.calculateRetrospect(lastMonth, thisMonth, member);

        // 총 수행도와 완료 수 계산
        int totalTodos = monthlyTodos.size();
        int completedTodos = (int) monthlyTodos.stream().filter(Todolist::isStatus).count();

        //회고 수
        int totalRetrospect = monthlyRetrospect.size();

        //<잘한점/부족한점/학습유형,빈도수>
        Map<String, Long> frequencyGood = analysisCalculator.calculateFrequencyGoodBadAndType(monthlyRetrospect, 1);
        Map<String, Long> frequencyBad = analysisCalculator.calculateFrequencyGoodBadAndType(monthlyRetrospect, 2);
        Map<String, Long> frequencyType = analysisCalculator.calculateFrequencyGoodBadAndType(monthlyRetrospect, 3);

        //전체 개수
        long totalGoodCount = frequencyGood.values().stream()
                .mapToLong(l->l).sum();
        long totalBadCount = frequencyBad.values().stream()
                .mapToLong(l->l).sum();
        long totalTypeCount = frequencyType.values().stream()
                .mapToLong(l->l).sum();

        //잘한점
        List<AnalysisDto.GoodResponse> topGoods = analysisCalculator.calculateSortedGoodBadAndType(frequencyGood,
                entry -> new AnalysisDto.GoodResponse(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalGoodCount * 100)),false);

        //부족한점
        List<AnalysisDto.BadResponse> topBads = analysisCalculator.calculateSortedGoodBadAndType(frequencyBad,
                entry -> new AnalysisDto.BadResponse(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalBadCount * 100)), false);

        //학습유형 가변리스트
        List<AnalysisDto.StudyTypeResponse> topType = new ArrayList<>(analysisCalculator.calculateSortedGoodBadAndType(frequencyType,
                entry -> new AnalysisDto.StudyTypeResponse(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalTypeCount * 100)),true));

        // 학습유형 나머지 항목 처리 : 합 -> 나머지로 %
        long otherCount = frequencyType.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .skip(4)  // 상위 4개를 제외
                .mapToLong(Map.Entry::getValue)
                .sum();

        //나머지항목 > 0: 리스트에 추가
        if (otherCount > 0) {
            int otherPercentage = (int) Math.round((double) otherCount / totalTypeCount * 100);
            topType.add(new AnalysisDto.StudyTypeResponse("Other", otherPercentage));
        }

        //수행도
        List<AnalysisDto.Achievement> achievements = new ArrayList<>();
        //이해도
        List<AnalysisDto.UnderstandingLevel> understandingLevels = new ArrayList<>();

        // 요일 순회
        for (LocalDate currentDate = lastMonth; currentDate.isBefore(thisMonth); currentDate = currentDate.plusDays(1)) {

            //요일
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // 해당 요일에 대한 수행도 평균 계산
            double avgProgress = analysisCalculator.calculateProgressAvg(monthlyRetrospect, dayOfWeek);
            achievements.add(new AnalysisDto.Achievement(dayOfWeek.toString(), (int) avgProgress));

            // 해당 요일에 대한 이해도 평균 계산
            double avgUnderstanding = analysisCalculator.calculateUnderstandingAvg(monthlyRetrospect, dayOfWeek);
            understandingLevels.add(new AnalysisDto.UnderstandingLevel(dayOfWeek.toString(), (int) avgUnderstanding));
        }

        //AI 개선점 키워드 분석
        List<String> feedbacks = monthlyRetrospect.stream()
                .map(Retrospect::getActionPlan)
                .toList();

        List<String> keywords = openAiService.extractKeywords(feedbacks);

        Map<String, Long> keywordFrequency = keywords.stream()
                .collect(Collectors.groupingBy(keyword -> keyword, Collectors.counting()));

        List<String> topKeywords = keywordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // 엔티티 저장
        WeeklyAnalysis weeklyAnalysis = WeeklyAnalysis.builder()
                .member(member)
                .startDate(lastMonth)
                .todos(totalTodos)
                .completedTodos(completedTodos)
                .retrospects(totalRetrospect)
                .totalStudyType(totalTypeCount)
                .startDate(lastMonth)
                .period(Period.MONTHLY)
                .build();

        analysisRepository.save(weeklyAnalysis);

        List<AnalysisGoodBad> analysisGoods = topGoods.stream()
                .map(good -> AnalysisGoodBad.builder()
                        .content(good.getContent())
                        .percentage(good.getPercentage())
                        .goodBad(GoodBad.GOOD)
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisGoodBadRepository.saveAll(analysisGoods);

        List<AnalysisGoodBad> analysisBads = topBads.stream()
                .map(bad -> AnalysisGoodBad.builder()
                        .content(bad.getContent())
                        .percentage(bad.getPercentage())
                        .goodBad(GoodBad.BAD)
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());

        analysisGoodBadRepository.saveAll(analysisBads);

        List<AnalysisStudyType> analysisStudyTypes = topType.stream()
                .map(type -> AnalysisStudyType.builder()
                        .type(type.getType())
                        .percentage(type.getPercentage())
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisStudyTypeRepository.saveAll(analysisStudyTypes);

        List<AnalysisUnderstandingAchievement> analysisAchievements = achievements.stream()
                .map(achievement -> AnalysisUnderstandingAchievement.builder()
                        .day(achievement.getDay())
                        .percentage(achievement.getPercentage())
                        .understandingAchievement(UnderstandingAchievement.ACHIEVEMENT)
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisUnderstandingAchievementRepository.saveAll(analysisAchievements);

        List<AnalysisUnderstandingAchievement> analysisUnderstandings = understandingLevels.stream()
                .map(understandingLevel -> AnalysisUnderstandingAchievement.builder()
                        .day(understandingLevel.getDay())
                        .percentage(understandingLevel.getPercentage())
                        .understandingAchievement(UnderstandingAchievement.UNDERSTANDING)
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisUnderstandingAchievementRepository.saveAll(analysisUnderstandings);

        List<Improvement> improvements = topKeywords.stream()
                .map(improvement -> Improvement.builder()
                        .content(improvement)
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        improvementRepository.saveAll(improvements);
    }

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getMonthlyAnalysisReport(String memberId, LocalDate date) {

        //현재 날짜보다 이후 날짜 조회시 예외 발생
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);
        }

        //한달이 지나지 않은 상태에서 조회시 예외 발생
        if (date.getYear() > today.getYear() ||
                (date.getYear() == today.getYear() && date.getMonth().getValue() >= today.getMonth().getValue())) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_ALREADY);
        }

        Member member = memberServiceHelper.findMemberByUuid(memberId);

        WeeklyAnalysis analysis = analysisRepository.findByMemberAndStartDate(member, date);

        // 분석 보고서가 없다면 예외 발생
        if (analysis == null) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);  // 적절한 예외 처리
        }

        // 미션
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        missionService.incrementMissionProgress(member.getId(), myPage, MONTHLY_REPORTER);

        // 알림
        sendMonthlyNotification(analysis.getStartDate().getMonthValue(), member);

        return AnalysisDto.AnalysisDtoResponse.fromEntity(analysis);
    }

    public void sendMonthlyNotification(int month, Member member) {
        notificationService.sendNotification(
                member.getId(),
                month + "월 월간 분석보고서가 도착했어요!",
                NotificationType.ANALYSIS
        );
    }
}
