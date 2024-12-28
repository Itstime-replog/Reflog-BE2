package itstime.reflog.analysis.service;

import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Bad;
import itstime.reflog.retrospect.domain.Good;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.repository.TodolistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final TodolistRepository todolistRepository;
    private final MemberRepository memberRepository;
    private final RetrospectRepository retrospectRepository;

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getWeeklyAnalysisReport(Long memberId, LocalDate date){

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

        //특정 날짜에 해당하는 주 데이터 조회
        LocalDate thisMonday = date.with(DayOfWeek.MONDAY);
        LocalDate nextMonday = thisMonday.plusDays(7);

        //투두
        List<Todolist> weeklyTodos = calculateTodo(thisMonday, nextMonday,member);

        int totalTodos = weeklyTodos.size();
        int completedTodos = (int) weeklyTodos.stream()
                .filter(todos -> todos.isStatus())
                .count();

        //회고
        List<Retrospect> weeklyRetrospect = calculateRetrospect(thisMonday, nextMonday, member);

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
        List<AnalysisDto.Good> topGoods = calculateSortedGoodBadAndType(frequencyGood,
                entry -> new AnalysisDto.Good(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalGoodCount * 100)),false);

        //부족한점
        List<AnalysisDto.Bad> topBads = calculateSortedGoodBadAndType(frequencyBad,
                entry -> new AnalysisDto.Bad(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalBadCount * 100)), false);

        //학습유형 가변리스트
        List<AnalysisDto.StudyType> topType = new ArrayList<>(calculateSortedGoodBadAndType(frequencyType,
                entry -> new AnalysisDto.StudyType(entry.getKey(),
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
            topType.add(new AnalysisDto.StudyType("Other", otherPercentage));
        }

        AnalysisDto.StudyTypes studyTypes = new AnalysisDto.StudyTypes((int) totalTypeCount, topType);

        //수행도
        List<AnalysisDto.Achievement> achievements = new ArrayList<>();
        //이해도
        List<AnalysisDto.UnderstandingLevel> understandingLevels = new ArrayList<>();

        // 요일 순회
        for (LocalDate currentDate = thisMonday; currentDate.isBefore(nextMonday); currentDate = currentDate.plusDays(1)) {

            //요일
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // 해당 요일에 대한 수행도 평균 계산
            double avgProgress = calculateProgressAvg(weeklyRetrospect, dayOfWeek);
            achievements.add(new AnalysisDto.Achievement(dayOfWeek.toString(), (int) avgProgress));

            // 해당 요일에 대한 이해도 평균 계산
            double avgUnderstanding = calculateUnderstandingAvg(weeklyRetrospect, dayOfWeek);
            understandingLevels.add(new AnalysisDto.UnderstandingLevel(dayOfWeek.toString(), (int) avgUnderstanding));
        }


        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos, totalRetrospect, topGoods, topBads,achievements,understandingLevels, studyTypes);
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

        List<Todolist> weeklyTodos = calculateTodo(startOfMonth, startOfNextMonth,member);

        int totalTodos = weeklyTodos.size();
        int completedTodos = (int) weeklyTodos.stream()
                .filter(todos -> todos.isStatus())
                .count();

        //회고
        List<Retrospect> monthlyRetrospect = calculateRetrospect(startOfMonth, startOfNextMonth, member);

        //회고 수
        int totalRetrospect = monthlyRetrospect.size();

        //<잘한점/부족한점/학습유형,빈도수>
        Map<String, Long> frequencyGood = calculateFrequencyGoodBadAndType(monthlyRetrospect, 1);
        Map<String, Long> frequencyBad = calculateFrequencyGoodBadAndType(monthlyRetrospect, 2);
        Map<String, Long> frequencyType = calculateFrequencyGoodBadAndType(monthlyRetrospect, 3);

        //전체 개수
        long totalGoodCount = frequencyGood.values().stream()
                .mapToLong(l->l).sum();
        long totalBadCount = frequencyBad.values().stream()
                .mapToLong(l->l).sum();
        long totalTypeCount = frequencyType.values().stream()
                .mapToLong(l->l).sum();

        //잘한점
        List<AnalysisDto.Good> topGoods = calculateSortedGoodBadAndType(frequencyGood,
                entry -> new AnalysisDto.Good(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalGoodCount * 100)),false);

        //부족한점
        List<AnalysisDto.Bad> topBads = calculateSortedGoodBadAndType(frequencyBad,
                entry -> new AnalysisDto.Bad(entry.getKey(),
                        (int) Math.round((double) entry.getValue() / totalBadCount * 100)), false);

        //학습유형 가변리스트
        List<AnalysisDto.StudyType> topType = new ArrayList<>(calculateSortedGoodBadAndType(frequencyType,
                entry -> new AnalysisDto.StudyType(entry.getKey(),
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
            topType.add(new AnalysisDto.StudyType("Other", otherPercentage));
        }

        AnalysisDto.StudyTypes studyTypes = new AnalysisDto.StudyTypes((int) totalTypeCount, topType);

        //수행도
        List<AnalysisDto.Achievement> achievements = new ArrayList<>();
        //이해도
        List<AnalysisDto.UnderstandingLevel> understandingLevels = new ArrayList<>();

        // 요일 순회
        for (LocalDate currentDate = startOfMonth; currentDate.isBefore(startOfNextMonth); currentDate = currentDate.plusDays(1)) {
            //요일
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

            // 해당 요일에 대한 수행도 평균 계산
            double avgProgress = calculateProgressAvg(monthlyRetrospect, dayOfWeek);
            achievements.add(new AnalysisDto.Achievement(dayOfWeek.toString(), (int) avgProgress));

            // 해당 요일에 대한 이해도 평균 계산
            double avgUnderstanding = calculateUnderstandingAvg(monthlyRetrospect, dayOfWeek);
            understandingLevels.add(new AnalysisDto.UnderstandingLevel(dayOfWeek.toString(), (int) avgUnderstanding));
        }
        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos, totalRetrospect, topGoods, topBads, achievements, understandingLevels, studyTypes);
    }

    private List<Todolist> calculateTodo(LocalDate start, LocalDate end, Member member){
        return todolistRepository.findByMember(member).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(start) &&
                                todo.getCreatedDate().isBefore(end)
                )
                .toList();
    }

    private List<Retrospect> calculateRetrospect(LocalDate start, LocalDate end, Member member){
        return retrospectRepository.findByMember(member).stream()
                .filter(retrospect ->
                        !retrospect.getCreatedDate().isBefore(start) &&
                                retrospect.getCreatedDate().isBefore(end)
                )
                .toList();
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
