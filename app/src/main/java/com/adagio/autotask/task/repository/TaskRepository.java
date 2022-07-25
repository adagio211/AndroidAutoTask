package com.adagio.autotask.task.repository;

import com.adagio.autotask.task.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * TaskRepository
 */
public interface TaskRepository {

    void insertTask(Task task);

    void updateTaskById(Task task);

//    void deleteByName(String taskName);

    Optional<Task> selectByName(String taskName);

    List<Task> selectAllTask();

    int selectAllCountTask();

    int selectCountTask(String taskName);

    void deleteAllTask();

    void deleteById(int id);

    Optional<Task> selectById(Integer id);
}
