package com.adagio.autotask.task.repository;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.MainActivity;
import com.adagio.autotask.db.DbHelper;
import com.adagio.autotask.task.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * TaskRepository impl
 */
public class LocalTaskRepositoryImpl implements TaskRepository {

    private DbHelper dbHelper;

//    @Inject
    public LocalTaskRepositoryImpl(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void insertTask(Task task) {
        dbHelper.insertTask(task);
    }

    @Override
    public void updateTaskById(Task task) {
        dbHelper.updateTaskById(task);
    }

    @Override
    public Optional<Task> selectByName(String taskName) {
        return dbHelper.selectTaskByName(taskName);
    }

    @Override
    public List<Task> selectAllTask() {
        return dbHelper.selectAllTask();
    }

    @Override
    public int selectAllCountTask() {
        return dbHelper.selectAllCountTask();
    }

    @Override
    public int selectCountTask(String taskName) {
        return dbHelper.selectCountTask(taskName);
    }

    @Override
    public void deleteAllTask() {
        dbHelper.deleteAllTask();
    }

    @Override
    public void deleteById(int id) {
        dbHelper.deleteTaskById(id);
    }

    @Override
    public Optional<Task> selectById(Integer id) {
        return dbHelper.selectTaskById(id);
    }
}
