package itstime.reflog.schedule.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.oauth.token.exception.TokenErrorResult;
import itstime.reflog.oauth.token.exception.TokenException;
import itstime.reflog.schedule.domain.Schedule;
import itstime.reflog.schedule.dto.ScheduleDto;
import itstime.reflog.schedule.repository.ScheduleRepository;
import itstime.reflog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void createSchedule(String authorizationHeader, ScheduleDto.ScheduleSaveOrUpdateRequest dto) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }

        String accessToken = jwtUtil.getTokenFromHeader(authorizationHeader);
        if (jwtUtil.isTokenExpired(accessToken)) {
            throw new TokenException(TokenErrorResult.INVALID_TOKEN);
        }

        String memberId = jwtUtil.getUserIdFromToken(accessToken);
        Member member = memberRepository.findByUuid(UUID.fromString(memberId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .allday(dto.isAllday())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .member(member)
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional
    public ScheduleDto.ScheduleResponse getSchedule(Long scheduleId, Long memberId){
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return new ScheduleDto.ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getAllday(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime()
        );
    }

    @Transactional
    public List<ScheduleDto.ScheduleAllResponse> getAllSchedule(Long memberId, int month) {

        List<Schedule> schedules = scheduleRepository.findByMemberAndStartDateTimeMonth(memberId, month);

        return schedules.stream()
                .map(schedule -> new ScheduleDto.ScheduleAllResponse(
                        schedule.getId(),
                        schedule.getTitle(),
                        schedule.getStartDateTime()
                        ))
                .toList();

    }

    @Transactional
    public void updateSchedule(Long scheduleId, Long memberId, ScheduleDto.ScheduleSaveOrUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        schedule.update(request, member);

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedule);
    }
}
