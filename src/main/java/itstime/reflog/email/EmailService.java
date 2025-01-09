package itstime.reflog.email;

import java.time.LocalDate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailService {
	private final JavaMailSender javaMailSender;
	private final SpringTemplateEngine templateEngine;
	private final RetrospectRepository retrospectRepository;
	private final MemberRepository memberRepository;

	@Async
	public void sendEmail(String email, String templateName, String date, String name) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(email); // 수신자
			mimeMessageHelper.setSubject(getSubjectByTemplate(templateName)); // 메일 제목
			mimeMessageHelper.setText(setContext(templateName, date, name), true); // 메일 본문

			javaMailSender.send(mimeMessage);
			log.info("Succeeded to send email to " + email);
		} catch (Exception e) {
			log.error("Failed to send email to " + email, e);
		}
	}

	private String getSubjectByTemplate(String templateName) {
		switch (templateName) {
			case "day":
				return "똑똑! 리플로그가 회고일지를 기다리고 있어요.";
			case "week":
				return "이번주도 리플로그가 응원해요! 하루 10분만 투자해보세요.";
			case "month":
				return "회고일지를 잊으셨나요? 다시 시작해보세요!";
			default:
				return "Reflog - 회고일지를 작성해주세요!";
		}
	}

	public String setContext(String templateName, String date, String name) {
		Context context = new Context();
		context.setVariable("date", date);
		context.setVariable("name", name);
		return templateEngine.process(templateName, context); // HTML 템플릿 처리
	}

	public LocalDate getLatestRetrospectDate(Long memberId) {
		return retrospectRepository.findLatestRetrospectDateByMemberId(memberId)
			.orElse(LocalDate.MIN); // 최신 회고일지 날짜 조회 (없으면 최소값 반환)
	}
}
