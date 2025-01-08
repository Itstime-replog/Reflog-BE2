package itstime.reflog.schedule.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.schedule.domain.Schedule;
import itstime.reflog.schedule.dto.ScheduleDto;
import itstime.reflog.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberServiceHelper memberServiceHelper;


    @Transactional
    public void createSchedule(String memberId, ScheduleDto.ScheduleSaveOrUpdateRequest dto) {

        Member member = memberServiceHelper.findMemberByUuid(memberId);


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
    public ScheduleDto.ScheduleResponse getSchedule(Long scheduleId, String memberId){
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        Member member = memberServiceHelper.findMemberByUuid(memberId);

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
    public List<ScheduleDto.ScheduleAllResponse> getAllSchedule(String memberId, int month) {
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        List<Schedule> schedules = scheduleRepository.findByMemberAndStartDateTimeMonth(member.getId(), month);

        return schedules.stream()
                .map(schedule -> new ScheduleDto.ScheduleAllResponse(
                        schedule.getId(),
                        schedule.getTitle(),
                        schedule.getStartDateTime()
                        ))
                .toList();

    }

    @Transactional
    public void updateSchedule(Long scheduleId, String memberId, ScheduleDto.ScheduleSaveOrUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        Member member = memberServiceHelper.findMemberByUuid(memberId);

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
