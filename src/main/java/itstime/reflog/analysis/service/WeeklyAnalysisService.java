package itstime.reflog.analysis.service;

import itstime.reflog.ai.service.OpenAiService;
import itstime.reflog.analysis.domain.*;
import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.analysis.repository.*;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.todolist.domain.Todolist;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyAnalysisService {

    private final MemberRepository memberRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisGoodBadRepository analysisGoodBadRepository;
    private final AnalysisStudyTypeRepository analysisStudyTypeRepository;
    private final AnalysisUnderstandingAchievementRepository analysisUnderstandingAchievementRepository;
    private final OpenAiService openAiService;
    private final ImprovementRepository improvementRepository;
    private final PeriodFilter periodFilter;

    @Transactional
    public void createWeeklyAnalysis(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.with(DayOfWeek.MONDAY).minusWeeks(1);
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

        // 투두, 회고, 기타 계산 수행
        List<Todolist> weeklyTodos = periodFilter.calculateTodo(lastMonday, thisMonday, member);
        List<Retrospect> weeklyRetrospect = periodFilter.calculateRetrospect(lastMonday, thisMonday, member);

        // 총 수행도와 완료 수 계산
        int totalTodos = weeklyTodos.size();
        int completedTodos = (int) weeklyTodos.stream().filter(Todolist::isStatus).count();

        //회고 수
        int totalRetrospect = weeklyRetrospect.size();

        //<잘한점/부족한점/학습유형,빈도수>
        Map<String, Long> frequencyGood = calculateFrequencyGoodBadAndType(weeklyRetrospect, 1);
        Map<String, Long> frequencyBad = calculateFrequencyGoodBadAndType(weeklyRetrospect, 2);
        Map<String, Long> frequencyType = calculateFrequencyGoodBadAndType(weeklyRetrospect, 3);

        //전체 개수
        long totalGoodCount = frequencyGood.values().stream()
                .mapToLong(l->l).sum();
        long totalBadCount = frequencyBad.values().stream()
                .mapToLong(l->l).sum();
        long totalTypeCount = frequencyType.values().stream()
                .mapToLong(l->l).sum();

        //잘한점
        List<AnalysisDto.GoodResponse> topGoods = calculateSortedGoodBadAndType(frequencyGood,
                entry -> new AnalysisDto.GoodResponse(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalGoodCount * 100)),false);

        //부족한점
        List<AnalysisDto.BadResponse> topBads = calculateSortedGoodBadAndType(frequencyBad,
                entry -> new AnalysisDto.BadResponse(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalBadCount * 100)), false);

        //학습유형 가변리스트
        List<AnalysisDto.StudyTypeResponse> topType = new ArrayList<>(calculateSortedGoodBadAndType(frequencyType,
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
        for (LocalDate currentDate = lastMonday; currentDate.isBefore(thisMonday); currentDate = currentDate.plusDays(1)) {

            //요일
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // 해당 요일에 대한 수행도 평균 계산
            double avgProgress = calculateProgressAvg(weeklyRetrospect, dayOfWeek);
            achievements.add(new AnalysisDto.Achievement(dayOfWeek.toString(), (int) avgProgress));

            // 해당 요일에 대한 이해도 평균 계산
            double avgUnderstanding = calculateUnderstandingAvg(weeklyRetrospect, dayOfWeek);
            understandingLevels.add(new AnalysisDto.UnderstandingLevel(dayOfWeek.toString(), (int) avgUnderstanding));
        }

        List<String> feedbacks = weeklyRetrospect.stream()
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
                .startDate(lastMonday)
                .todos(totalTodos)
                .completedTodos(completedTodos)
                .retrospects(totalRetrospect)
                .totalStudyType(totalTypeCount)
                .startDate(lastMonday)
                .build();

        analysisRepository.save(weeklyAnalysis);

        List<AnalysisGoodBad> analysisGoods = topGoods.stream()
                .map(good -> AnalysisGoodBad.builder()
                        .content(good.getContent())
                        .percentage(good.getPercentage())
                        .type("good")
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisGoodBadRepository.saveAll(analysisGoods);

        List<AnalysisGoodBad> analysisBads = topBads.stream()
                .map(bad -> AnalysisGoodBad.builder()
                        .content(bad.getContent())
                        .percentage(bad.getPercentage())
                        .type("bad")
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
                        .type("achievement")
                        .weeklyAnalysis(weeklyAnalysis) // 연관관계 설정
                        .build())
                .collect(Collectors.toList());
        analysisUnderstandingAchievementRepository.saveAll(analysisAchievements);

        List<AnalysisUnderstandingAchievement> analysisUnderstandings = understandingLevels.stream()
                .map(understandingLevel -> AnalysisUnderstandingAchievement.builder()
                        .day(understandingLevel.getDay())
                        .percentage(understandingLevel.getPercentage())
                        .type("understanding")
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
    public AnalysisDto.AnalysisDtoResponse getWeeklyAnalysisReport(Long memberId, LocalDate date) {

        //현재 날짜보다 이후 날짜 조회시 예외 발생
        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);
        }

        //일주일이 지나지 않은 상태에서 조회시 예외 발생
        if (date.isAfter(today.with(DayOfWeek.MONDAY).minusDays(1))) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_ALREADY);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        WeeklyAnalysis analysis = analysisRepository.findByMemberAndStartDate(member, date);

        // 분석 보고서가 없다면 예외 발생
        if (analysis == null) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);  // 적절한 예외 처리
        }

        return AnalysisDto.AnalysisDtoResponse.fromEntity(analysis);
    }


    private Map<String, Long> calculateFrequencyGoodBadAndType(List<Retrospect> retrospects, int badOrGoodOrType) {
        return switch (badOrGoodOrType) {
            case 1 -> retrospects.stream()
                    .flatMap(retrospect -> retrospect.getGoods().stream()
                            .map(good -> good.getContent()))
                    .collect(Collectors.groupingBy(good -> good, Collectors.counting()));
            case 2 -> retrospects.stream()
                    .flatMap(retrospect -> retrospect.getBads().stream()
                            .map(bad -> bad.getContent()))
                    .collect(Collectors.groupingBy(bad -> bad, Collectors.counting()));
            case 3 -> retrospects.stream()
                    .flatMap(retrospect -> retrospect.getStudyTypes().stream()
                            .map(type -> type.getType()))
                    .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
            default -> throw new IllegalArgumentException("Invalid type: " + badOrGoodOrType);
        };
    }

    private <T> List<T> calculateSortedGoodBadAndType(Map<String,Long> frequency, Function<Map.Entry<String, Long>, T> mapper, boolean typeOrOther) {
        if(typeOrOther) {
            return frequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(4) //상위 4개
                    .map(mapper) // 제네릭 변환
                    .toList();
        }
        else {
            return frequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(mapper)
                    .toList();
        }
    }

    private double calculateProgressAvg(List<Retrospect> retrospects, DayOfWeek day) {
        return retrospects.stream()
                .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                .mapToInt(Retrospect::getProgressLevel) // 수행도 추출
                .average()
                .orElse(0); // 없으면 0
    }

    // 이해도 평균 계산 (Understanding Level)
    private double calculateUnderstandingAvg(List<Retrospect> retrospects, DayOfWeek day) {
        return retrospects.stream()
                .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                .mapToInt(Retrospect::getUnderstandingLevel) // 이해도 추출
                .average()
                .orElse(0); // 없으면 0
    }

}
