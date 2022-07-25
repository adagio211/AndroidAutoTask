package com.adagio.autotask.task.service;

import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.task.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

//@AndroidEntryPoint
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;

//    @Inject
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Override
    public void saveOrUpdateTask(Task task) {
        if (task.getId() != null) {
            taskRepository.updateTaskById(task);
        } else {
            taskRepository.insertTask(task);
        }
    }

//    @Override
//    public void deleteByTaskName(String name) {
//        taskRepository.deleteByName(name);
//    }

    @Override
    public void deleteByTaskId(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Optional<Task> selectByName(String taskName) {
        return taskRepository.selectByName(taskName);
    }

    @Override
    public List<Task> selectAllTask() {
        return taskRepository.selectAllTask();
    }

    @Override
    public int selectAllCountTask() {
        return taskRepository.selectAllCountTask();
    }

    @Override
    public int selectCountTask(String taskName) {
        return taskRepository.selectCountTask(taskName);
    }

    @Override
    public void deleteAllTask() {
        taskRepository.deleteAllTask();
    }

    @Override
    public Optional<Task> selectById(Integer id) {
        return taskRepository.selectById(id);
    }
}
