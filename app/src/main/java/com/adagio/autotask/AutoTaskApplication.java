package com.adagio.autotask;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import com.adagio.autotask.db.DbHelper;
import com.adagio.autotask.task.repository.LocalTaskRepositoryImpl;
import com.adagio.autotask.task.repository.TaskRepository;
import com.adagio.autotask.task.service.TaskService;
import com.adagio.autotask.task.service.TaskServiceImpl;
import com.adagio.autotask.view.ScreenFocusView;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@HiltAndroidApp
public class AutoTaskApplication extends Application {
    private String TAG = getClass().getSimpleName();

    public static final ExecutorService TASK_EXECUTION_POOL = Executors.newSingleThreadExecutor();
    private static DbHelper dbHelper;

    private static TaskService taskService;
    private static TaskRepository taskRepository;

    private final Map<String, List<Dialog>> applicationDialog = new HashMap<>();
    private static ScreenFocusView screenFocusView = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "CREATE =======");
        dbHelper = new DbHelper(getApplicationContext());
        taskRepository = new LocalTaskRepositoryImpl(dbHelper);
        taskService = new TaskServiceImpl(taskRepository);

        // 初始化 Toast 框架
        ToastUtils.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        dbHelper.close();
    }

    public void addDialog(String dialogName, Dialog dialog) {
        List<Dialog> dialogList = applicationDialog.get(dialogName);
        if (dialogList == null) {
            dialogList = new ArrayList<>();
        }
        dialogList.add(dialog);
        applicationDialog.put(dialogName, dialogList);
    }

    public void removeDialog(String dialogName) {
        if (!applicationDialog.containsKey(dialogName)) return;
        final List<Dialog> dialogList = applicationDialog.get(dialogName);
        if (dialogList != null && dialogList.size() > 0) {
            dialogList.forEach(Dialog::dismiss);
        }
        applicationDialog.remove(dialogName);
    }

    public static ScreenFocusView getScreenFocusView() {
        return screenFocusView;
    }

    public static void setScreenFocusView(ScreenFocusView screenFocusView) {
        if (AutoTaskApplication.screenFocusView != null) {
            AutoTaskApplication.screenFocusView.destroy();
        }
        AutoTaskApplication.screenFocusView = screenFocusView;
    }

    public static DbHelper getDbHelper() {
        return dbHelper;
    }

    public static TaskService getTaskService() {
        return taskService;
    }
}
