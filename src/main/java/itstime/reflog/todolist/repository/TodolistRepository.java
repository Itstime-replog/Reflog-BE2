package itstime.reflog.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.todolist.domain.Todolist;

public interface TodolistRepository extends JpaRepository<Todolist, Integer> {

}
