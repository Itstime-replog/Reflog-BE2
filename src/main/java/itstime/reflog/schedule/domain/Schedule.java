package itstime.reflog.schedule.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import itstime.reflog.member.domain.Member;
import itstime.reflog.schedule.dto.ScheduleDto;
import itstime.reflog.todolist.dto.TodolistDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Schedule {

    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    private Boolean allday;

//    @Column(name = "created_date", nullable = false)
//    private LocalDate createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void update(ScheduleDto.ScheduleSaveOrUpdateRequest request, Member member) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.allday = request.isAllday();
        this.startDateTime = request.getStartDateTime();
        this.endDateTime = request.getEndDateTime();
        this.member = member;
    }
}
