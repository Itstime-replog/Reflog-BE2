package itstime.reflog.schedule.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.repository.NotificationRepository;
import itstime.reflog.notification.service.NotificationService;
import itstime.reflog.schedule.domain.Schedule;
import itstime.reflog.schedule.dto.ScheduleDto;
import itstime.reflog.schedule.repository.ScheduleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @PostConstruct
    public void ScheduleService() {
        taskScheduler.initialize(); // Initialize the scheduler
    }

    @Transactional
    public void createSchedule(String memberId, ScheduleDto.ScheduleSaveOrUpdateRequest dto) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 일정 생성
        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .allday(dto.isAllday())
                .isOn(dto.getIsOn())
                .startDateTime(dto.getStartDateTime())
                .endDateTime(dto.getEndDateTime())
                .member(member)
                .build();

        scheduleRepository.save(schedule);

        // 3. 알림
        if(dto.getIsOn()){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDateTime = dto.getStartDateTime();

            long delay = Duration.between(now, startDateTime).toMillis();

            if (delay <= 0) {
                throw new GeneralException(ErrorStatus._DELAY);
            }

            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> sendScheduleNotification(member.getId(), schedule.getTitle()),
                    new java.util.Date(System.currentTimeMillis() + delay)
            );

            scheduledTasks.put(member.getId(), future);
        }
    }

    @Transactional
    public ScheduleDto.ScheduleResponse getSchedule(Long scheduleId, String memberId){
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 일정 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        return new ScheduleDto.ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getAllday(),
                schedule.getIsOn(),
                schedule.getStartDateTime(),
                schedule.getEndDateTime()
        );
    }

    @Transactional
    public List<ScheduleDto.ScheduleAllResponse> getAllSchedule(String memberId, int month) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 일정 전체 조회
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
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 일정 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        // 3. 일정 업데이트
        schedule.update(request, member);

        scheduleRepository.save(schedule);

        // 4. 알림
        LocalDateTime startDateTime = request.getStartDateTime();
        LocalDateTime now = LocalDateTime.now();

        if (!request.getIsOn()) {
            // 알림이 존재하면 삭제
            if (scheduledTasks.containsKey(member.getId())) {
                ScheduledFuture<?> future = scheduledTasks.get(member.getId());
                if (!future.isDone()) {
                    future.cancel(true);
                    scheduledTasks.remove(member.getId());
                }
            }
            return;
        }

        // isOn이 true
        if (scheduledTasks.containsKey(member.getId())) {
            ScheduledFuture<?> future = scheduledTasks.get(member.getId());
            if (!future.isDone()) {
                future.cancel(true);
            }
        }

        if (startDateTime.isAfter(now)) {
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> sendScheduleNotification(member.getId(), schedule.getTitle()),
                    java.util.Date.from(startDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant())
            );
            scheduledTasks.put(member.getId(), future);
        }
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        // 1. 일정 조회
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SCHEDULE_NOT_FOUND));

        // 2. 일정 삭제
        scheduleRepository.delete(schedule);

        // 3. 알림 삭제
        if (scheduledTasks.containsKey(schedule.getMember().getId())) {
            ScheduledFuture<?> future = scheduledTasks.get(schedule.getMember().getId());
            future.cancel(true);
            scheduledTasks.remove(schedule.getMember().getId());

            notificationRepository.deleteByMemberId(schedule.getMember().getId());
        }
    }

    private void sendScheduleNotification(Long memberId, String title) {
        notificationService.sendNotification(
                memberId,
                "오늘은 " + title + " D-day 입니다!",
                NotificationType.SCHEDULE,
                "api/v1/plan/schedule"
        );
    }
}
