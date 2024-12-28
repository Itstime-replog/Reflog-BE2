package itstime.reflog.retrospect.repository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.todolist.domain.Todolist;
import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.retrospect.domain.Retrospect;

import java.util.List;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {
    List<Retrospect> findByMember(Member member);
}
