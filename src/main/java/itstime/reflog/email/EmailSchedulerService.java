package itstime.reflog.email;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class EmailSchedulerService {
	private final EmailService emailService;
	private final MemberRepository memberRepository;

	@Scheduled(cron = "0 0 12 * * *") // 매일 12시 실행
	public void sendDailyEmail() {
		List<Member> members = memberRepository.findAll();
		for (Member member : members) {
			LocalDate latestDate = emailService.getLatestRetrospectDate(member.getId());
			LocalDate today = LocalDate.now();

			// 최신 날짜와 오늘 날짜 차이 계산
			long daysDifference = ChronoUnit.DAYS.between(latestDate, today);

			if (daysDifference == 2) { // 이틀 차이
				emailService.sendEmail(member.getEmail(), "day", today.toString(), member.getName());
			}
		}
	}

	@Scheduled(cron = "0 0 12 * * MON") // 매주 월요일 12시 실행
	public void sendWeeklyEmail() {
		List<Member> members = memberRepository.findAll();
		for (Member member : members) {
			LocalDate latestDate = emailService.getLatestRetrospectDate(member.getId());
			LocalDate today = LocalDate.now();

			long daysDifference = ChronoUnit.DAYS.between(latestDate, today);

			if (daysDifference > 2 && daysDifference < 30) { // 이틀 이상, 한 달 미만
				emailService.sendEmail(member.getEmail(), "week", today.toString(), member.getName());
			}
		}
	}

	@Scheduled(cron = "0 0 12 1 * *") // 매월 1일 12시 실행
	public void sendMonthlyEmail() {
		List<Member> members = memberRepository.findAll();
		for (Member member : members) {
			LocalDate latestDate = emailService.getLatestRetrospectDate(member.getId());
			LocalDate today = LocalDate.now();

			long daysDifference = ChronoUnit.DAYS.between(latestDate, today);

			if (daysDifference >= 30) { // 한 달 이상
				emailService.sendEmail(member.getEmail(), "month", today.toString(), member.getName());
			}
		}
	}
}
