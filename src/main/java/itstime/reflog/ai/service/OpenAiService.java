package itstime.reflog.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itstime.reflog.ai.dto.OpenAiRequest;
import itstime.reflog.ai.dto.OpenAiResponse;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate;  // RestTemplate을 통해 API 호출

    // OpenAI 키 설정
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public List<String> extractKeywords(List<String> feedbacks) {
        // 피드백 텍스트를 합침
        String feedbacksText = String.join(". ", feedbacks);
        String prompt = "각각의 피드백과 관련있는 키워드를 목표 설정, 계획 조정, 우선순위, 실천, 시간 배분, 재설정, 점검, 평가, 일관성, 개선, 소통, 협업 이 예시 안에서 10개 정도 뽑아줘. 키워드는 ','로 구분해줘: \""
                + feedbacksText + "\".";

        // OpenAI API 호출을 위한 ChatGPTRequest 객체 생성
        OpenAiRequest chatGPTRequest = new OpenAiRequest("gpt-3.5-turbo", prompt);

        // API 요청을 보냄
        OpenAiResponse chatGPTResponse = restTemplate.postForObject(OPENAI_API_URL, chatGPTRequest, OpenAiResponse.class);

        // 첫 번째 선택지에서 응답 내용 가져오기
        String response = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        // 응답에서 쉼표(,)로 구분된 키워드를 처리하고 리스트로 반환
        return Arrays.stream(response.split(","))
                .map(String::trim)  // 키워드 앞뒤 공백 제거
                .filter(keyword -> !keyword.isEmpty())  // 빈 키워드는 필터링
                .toList();
    }
}