package itstime.reflog.todolist.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.todolist.domain.Todolist;

public interface TodolistRepository extends JpaRepository<Todolist, Long> {

	List<Todolist> findByMemberAndCreatedDate(Member member, LocalDate date);
	List<Todolist> findByMember(Member member);
}
