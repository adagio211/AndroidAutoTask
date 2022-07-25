package com.adagio.autotask.task.service;

import com.adagio.autotask.task.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    void saveOrUpdateTask(Task task);

//    void deleteByTaskName(String name);

    void deleteByTaskId(int id);

    Optional<Task> selectByName(String taskName);

    List<Task> selectAllTask();

    int selectAllCountTask();

    int selectCountTask(String taskName);

    void deleteAllTask();

    Optional<Task> selectById(Integer id);
}
