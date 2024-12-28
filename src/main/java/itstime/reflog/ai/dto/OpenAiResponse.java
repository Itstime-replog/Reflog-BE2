package itstime.reflog.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiResponse {
    private List<Choice> choices;  // 모델이 생성한 여러 선택지를 담은 리스트

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;        // 선택지 인덱스
        private Message message;  // 해당 선택지의 메시지 내용
    }
}
