package itstime.reflog.analysis.service;

import itstime.reflog.retrospect.domain.Retrospect;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalysisCalculator {

    public Map<String, Long> calculateFrequencyGoodBadAndType(List<Retrospect> retrospects, int badOrGoodOrType) {
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

    public  <T> List<T> calculateSortedGoodBadAndType(Map<String,Long> frequency, Function<Map.Entry<String, Long>, T> mapper, boolean typeOrOther) {
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

    public double calculateProgressAvg(List<Retrospect> retrospects, DayOfWeek day) {
        return retrospects.stream()
                .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                .mapToInt(Retrospect::getProgressLevel) // 수행도 추출
                .average()
                .orElse(0); // 없으면 0
    }

    // 이해도 평균 계산 (Understanding Level)
    public double calculateUnderstandingAvg(List<Retrospect> retrospects, DayOfWeek day) {
        return retrospects.stream()
                .filter(retrospect -> retrospect.getCreatedDate().getDayOfWeek() == day)
                .mapToInt(Retrospect::getUnderstandingLevel) // 이해도 추출
                .average()
                .orElse(0); // 없으면 0
    }
}
