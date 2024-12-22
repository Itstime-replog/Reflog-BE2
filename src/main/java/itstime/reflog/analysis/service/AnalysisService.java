package itstime.reflog.analysis.service;

import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Bad;
import itstime.reflog.retrospect.domain.Good;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.repository.TodolistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AnalysisService {

    private final TodolistRepository todolistRepository;
    private final MemberRepository memberRepository;
    private final RetrospectRepository retrospectRepository;

    public AnalysisService(TodolistRepository todolistRepository, MemberRepository memberRepository, RetrospectRepository retrospectRepository){
        this.todolistRepository = todolistRepository;
        this.memberRepository = memberRepository;
        this.retrospectRepository = retrospectRepository;
    }

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getWeeklyAnalysisReport(Long memberId, LocalDate date){

        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);
        }
        if (date.isAfter(today.with(DayOfWeek.MONDAY).minusDays(1))) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_ALREADY);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        //특정 날짜에 해당하는 주 데이터 조회
        LocalDate thisMonday = date.with(DayOfWeek.MONDAY);
        LocalDate nextMonday = thisMonday.plusDays(7);

        //투두
        List<Todolist> weeklyTodos = todolistRepository.findByMember(member).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(thisMonday) &&
                                todo.getCreatedDate().isBefore(nextMonday)
                )
                .toList();

        int totalTodos = weeklyTodos.size();
        int completedTodos = (int) weeklyTodos.stream()
                .filter(Todolist::isStatus)
                .count();

        //회고
        List<Retrospect> weeklyRetrospect = retrospectRepository.findByMember(member).stream()
                .filter(retrospect ->
                        !retrospect.getCreatedDate().isBefore(thisMonday) &&
                                retrospect.getCreatedDate().isBefore(nextMonday)
                )
                .toList();
        //회고 수
        int totalRetrospect = weeklyRetrospect.size();

        //잘한점
        Map<String, Long> goodsFrequency = weeklyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getGoods().stream()
                        .map(Good::getContent)
                ) .collect(Collectors.groupingBy(good -> good, Collectors.counting())); //같은 good 카운팅

        long totalGoodsCount = goodsFrequency.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        List<AnalysisDto.Good> topGoods = goodsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) //상위 3개
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalGoodsCount * 100);
                    return new AnalysisDto.Good(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        //부족한점
        Map<String, Long> badsFrequency = weeklyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getBads().stream()
                        .map(bad -> bad.getContent()))
                .collect(Collectors.groupingBy(bad -> bad, Collectors.counting()));
        long totalBadsCount = badsFrequency.values().stream()
                .mapToLong(Long::longValue).sum();

        List<AnalysisDto.Bad> topBads = badsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalBadsCount * 100);
                    return new AnalysisDto.Bad(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        //학습유형
        Map<String, Long> typeFrequency = weeklyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getStudyTypes().stream()
                        .map(type -> type.getType()))
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
        long totalTypeCount = typeFrequency.values().stream()
                .mapToLong(Long::longValue).sum();

        List<Map.Entry<String, Long>> sortedEntries = typeFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());

        List<AnalysisDto.StudyType> topType = sortedEntries.stream()
                .limit(4)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalTypeCount * 100);
                    return new AnalysisDto.StudyType(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        // 나머지 항목 처리
        long otherCount = sortedEntries.stream()
                .skip(4)
                .mapToLong(Map.Entry::getValue)
                .sum();

        if (otherCount > 0) {
            int otherPercentage = (int) Math.round((double) otherCount / totalTypeCount * 100);
            topType.add(new AnalysisDto.StudyType("Other", otherPercentage));
        }


        //수행도
        List<AnalysisDto.Achievement> achievements =
                Arrays.stream(DayOfWeek.values()) // 모든 요일을 순회,, 회고없는날 수행도0
                        .map(day -> {
                            // 주어진 요일의 수행도 가져오기 (없으면 0)
                            int progress = weeklyRetrospect.stream()
                                    .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                                    .mapToInt(Retrospect::getProgressLevel)
                                    .findFirst() // 첫 번째 수행도 가져오기
                                    .orElse(0); // 없으면 0 반환
                            return new AnalysisDto.Achievement(day.toString(), progress);
                        })
                        .collect(Collectors.toList());

        //이해도
        List<AnalysisDto.UnderstandingLevel> understandingLevels =
                Arrays.stream(DayOfWeek.values())
                        .map(day -> {
                            int understanding = weeklyRetrospect.stream()
                                    .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                                    .mapToInt(Retrospect::getUnderstandingLevel)
                                    .findFirst()
                                    .orElse(0);
                            return new AnalysisDto.UnderstandingLevel(day.toString(), understanding);
                        })
                        .collect(Collectors.toList());


        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos, totalRetrospect, topGoods, topBads,achievements,understandingLevels, topType);
    }

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getMonthlyAnalysisReport(Long memberId, Integer month) {
        LocalDate today = LocalDate.now();

        if (month > today.getMonthValue()) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);
        }

        // 이번 달의 분석은 불가능
        if (month == today.getMonthValue() && today.getYear() == today.getYear()) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_ALREADY);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 현재 연도를 기준으로 특정 월의 첫날과 다음 달 첫날을 계산
        LocalDate startOfMonth = LocalDate.of(today.getYear(), month, 1);
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        // 투두 조회
        List<Todolist> monthlyTodos = todolistRepository.findByMember(member).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(startOfMonth) &&
                                todo.getCreatedDate().isBefore(startOfNextMonth)
                )
                .toList();

        int totalTodos = monthlyTodos.size();
        int completedTodos = (int) monthlyTodos.stream()
                .filter(Todolist::isStatus)
                .count();

        // 회고 조회
        List<Retrospect> monthlyRetrospect = retrospectRepository.findByMember(member).stream()
                .filter(retrospect ->
                        !retrospect.getCreatedDate().isBefore(startOfMonth) &&
                                retrospect.getCreatedDate().isBefore(startOfNextMonth)
                )
                .toList();

        int totalRetrospect = monthlyRetrospect.size();

        // 잘한 점 분석
        Map<String, Long> goodsFrequency = monthlyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getGoods().stream()
                        .map(Good::getContent))
                .collect(Collectors.groupingBy(good -> good, Collectors.counting()));

        long totalGoodsCount = goodsFrequency.values().stream().mapToLong(Long::longValue).sum();

        List<AnalysisDto.Good> topGoods = goodsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalGoodsCount * 100);
                    return new AnalysisDto.Good(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        // 부족한 점 분석
        Map<String, Long> badsFrequency = monthlyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getBads().stream()
                        .map(Bad::getContent))
                .collect(Collectors.groupingBy(bad -> bad, Collectors.counting()));

        long totalBadsCount = badsFrequency.values().stream().mapToLong(Long::longValue).sum();

        List<AnalysisDto.Bad> topBads = badsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalBadsCount * 100);
                    return new AnalysisDto.Bad(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        // 학습 유형 분석
        Map<String, Long> typeFrequency = monthlyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getStudyTypes().stream()
                        .map(type -> type.getType()))
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        long totalTypeCount = typeFrequency.values().stream().mapToLong(Long::longValue).sum();

        List<Map.Entry<String, Long>> sortedEntries = typeFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());

        List<AnalysisDto.StudyType> topType = sortedEntries.stream()
                .limit(4)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalTypeCount * 100);
                    return new AnalysisDto.StudyType(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        long otherCount = sortedEntries.stream()
                .skip(4)
                .mapToLong(Map.Entry::getValue)
                .sum();

        if (otherCount > 0) {
            int otherPercentage = (int) Math.round((double) otherCount / totalTypeCount * 100);
            topType.add(new AnalysisDto.StudyType("Other", otherPercentage));
        }

        // 수행도
        List<AnalysisDto.Achievement> achievements = Arrays.stream(DayOfWeek.values()) // 요일 순회
                .map(day -> {
                    // 해당 요일에 대한 수행도 평균 계산
                    double avgProgress = monthlyRetrospect.stream()
                            .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                            .mapToInt(Retrospect::getProgressLevel)
                            .average()
                            .orElse(0); // 없으면 0으로 처리
                    return new AnalysisDto.Achievement(day.toString(), (int) avgProgress); // 소수점 없이 정수로 반환
                })
                .collect(Collectors.toList());

        //이해도
        List<AnalysisDto.UnderstandingLevel> understandingLevels = Arrays.stream(DayOfWeek.values()) // 요일 순회
                .map(day -> {
                    // 해당 요일에 대한 이해도 평균 계산
                    double avgUnderstanding = monthlyRetrospect.stream()
                            .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                            .mapToInt(Retrospect::getUnderstandingLevel)
                            .average()
                            .orElse(0); // 없으면 0으로 처리
                    return new AnalysisDto.UnderstandingLevel(day.toString(), (int) avgUnderstanding); // 소수점 없이 정수로 반환
                })
                .collect(Collectors.toList());

        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos, totalRetrospect, topGoods, topBads, achievements, understandingLevels, topType);
    }

}
