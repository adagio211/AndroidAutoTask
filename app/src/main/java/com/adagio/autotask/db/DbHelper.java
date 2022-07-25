package com.adagio.autotask.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adagio.autotask.motion.Action;
import com.adagio.autotask.motion.ActionType;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "auto_task_" + DATABASE_VERSION + ".db";
    public static final String TASK_TABLE_NAME = "task_execution";
    public static final String TASK_NAME_COLUMN_NAME = "task_name";
    public static final String CREATE_DATE_COLUMN_NAME = "create_date";
    public static final String UPDATE_DATE_COLUMN_NAME = "update_date";
    public static final String ACTIONS_JSON_COLUMN_NAME = "actions_json";
    public static final String TASK_DESCRIPTION_COLUMN_NAME = "task_description";
    public static final String ALWAYS_EXECUTION_COLUMN_NAME = "always_execution";
    public static final String EXECUTION_TIMES_COLUMN_NAME = "execution_times";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ALWAYS_EXECUTION_COLUMN_NAME + " int, " + EXECUTION_TIMES_COLUMN_NAME + " int, " +
                TASK_NAME_COLUMN_NAME + " TEXT, " + TASK_DESCRIPTION_COLUMN_NAME + " TEXT, " +
                        CREATE_DATE_COLUMN_NAME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + UPDATE_DATE_COLUMN_NAME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                ACTIONS_JSON_COLUMN_NAME + " TEXT) ");
        db.execSQL("CREATE UNIQUE INDEX IDX_TASK_NAME ON " + TASK_TABLE_NAME + "(" + TASK_NAME_COLUMN_NAME + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertTask(Task task) {
        final String insertSql = "INSERT INTO " + TASK_TABLE_NAME + "(" + TASK_NAME_COLUMN_NAME + ", " +
                TASK_DESCRIPTION_COLUMN_NAME + ", " + ALWAYS_EXECUTION_COLUMN_NAME + ", " + EXECUTION_TIMES_COLUMN_NAME + ", " +
                ACTIONS_JSON_COLUMN_NAME + ") values (?,?,?,?,?)";
        getWritableDatabase().execSQL(insertSql, new Object[] {
                task.getTaskName(),
                task.getTaskDescription(),
                task.getIsAlwaysExecution(),
                task.getExecutionTimes(),
                JsonUtil.toJson(task.getActions())});
    }

    public void updateTaskById(Task task) {
        final String updateSql = "UPDATE " + TASK_TABLE_NAME + " SET " + TASK_NAME_COLUMN_NAME + " =?, " +
                TASK_DESCRIPTION_COLUMN_NAME + "=?, " + ALWAYS_EXECUTION_COLUMN_NAME + "=?, " + EXECUTION_TIMES_COLUMN_NAME + "=?, " +
                ACTIONS_JSON_COLUMN_NAME + "=? WHERE ID=? ";
        getWritableDatabase().execSQL(updateSql, new Object[] {
                task.getTaskName(),
                task.getTaskDescription(),
                task.getIsAlwaysExecution(),
                task.getExecutionTimes(),
                JsonUtil.toJson(task.getActions()), task.getId()});
    }

    public Optional<Task> selectTaskById(Integer taskId) {
        final Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " WHERE id=?", new String[] {String.valueOf(taskId)});
        return Optional.of(buildTaskFromCursor(cursor).get(0));
    }

    @NonNull
    @SuppressLint("Range")
    private List<Task> buildTaskFromCursor(Cursor cursor) {
        final List<Task> rst = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String taskName = cursor.getString(cursor.getColumnIndex(TASK_NAME_COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION_COLUMN_NAME));
            String actionsJson = cursor.getString(cursor.getColumnIndex(ACTIONS_JSON_COLUMN_NAME));
            int executionTimes = cursor.getInt(cursor.getColumnIndex(EXECUTION_TIMES_COLUMN_NAME));
            short isAlwaysExecution = cursor.getShort(cursor.getColumnIndex(ALWAYS_EXECUTION_COLUMN_NAME));

            final List<Map<String, String>> actionsMapList = JsonUtil.toListMap(actionsJson);

            List<Action> actions = null;
            if (actionsMapList != null && actionsMapList.size() > 0) {
                actions = actionsMapList.stream().map(m -> {
                    final String actionType = m.get("type");
                    return JsonUtil.toObject(JsonUtil.toJson(m), ActionType.valueOf(actionType).getTypeClass());
                }).collect(Collectors.toList());
            }
//            cursor.close();
            Task task = new Task(id, taskName, description, actions);
            task.setIsAlwaysExecution(isAlwaysExecution);
            task.setExecutionTimes(executionTimes);
            rst.add(task);
        }
        cursor.close();
        return rst;
    }

    public Optional<Task> selectTaskByName(String name) {
        final Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + TASK_NAME_COLUMN_NAME + "=?", new String[] {name});
        return Optional.of(buildTaskFromCursor(cursor).get(0));
    }

    public List<Task> selectAllTask() {
        final List<Task> rst = new ArrayList<>();
        final Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_TABLE_NAME + " ORDER BY ID DESC", null);
        return buildTaskFromCursor(cursor);

//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(cursor.getColumnIndex("id"));
//            String taskName = cursor.getString(cursor.getColumnIndex(TASK_NAME_COLUMN_NAME));
//            String description = cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION_COLUMN_NAME));
//            String actionsJson = cursor.getString(cursor.getColumnIndex(ACTIONS_JSON_COLUMN_NAME));
//
//            final List<Map<String, String>> actionsMapList = JsonUtil.toListMap(actionsJson);
//
//            List<Action> actions = null;
//            if (actionsMapList != null && actionsMapList.size() > 0) {
//                actions = actionsMapList.stream().map(m -> {
//                    final String actionType = m.get("type");
//                    return  JsonUtil.toObject(JsonUtil.toJson(m), ActionType.valueOf(actionType).getTypeClass());
//                }).collect(Collectors.toList());
//            }
//            rst.add(new Task(id, taskName, description, actions));
//        }
//        cursor.close();
//        return rst;
    }

    public int selectAllCountTask() {
        final Cursor cursor = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TASK_TABLE_NAME, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        cursor.close();
        return 0;
    }

    public int selectCountTask(String taskName) {
        final Cursor cursor = this.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TASK_TABLE_NAME + " WHERE " + TASK_NAME_COLUMN_NAME + "=?", new String[] {taskName});
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        cursor.close();
        return 0;
    }

    public void deleteAllTask() {
        this.getWritableDatabase().delete(TASK_TABLE_NAME, null, null);
    }

    public void deleteTaskById(int id) {
        getWritableDatabase().execSQL("DELETE FROM " + TASK_TABLE_NAME + " WHERE ID=?", new Object[] {id});
    }
}
