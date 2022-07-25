package com.adagio.autotask.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.R;
import com.adagio.autotask.motion.Action;
import com.adagio.autotask.motion.Pointer;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.view.ActionParamView;
import com.adagio.autotask.view.ActionSelectionView;
import com.adagio.autotask.view.TaskInfoView;
import com.adagio.autotask.view.TaskMainView;

/**
 * 对话框工具
 */
public class DialogUtil {
    private static final String DIALOG_NAME_TASK_EXECUTION_DIALOG = "taskExecutionDilog";
    private static final String DIALOG_NAME_APP_MAIN = "appMainDialog";
    private static final String DIALOG_NAME_TASK_DIALOG = "taskInfoDialog";
    private static final String DIALOG_NAME_ACTION_SELECTION_DIALOG = "actionSelectionDialog";
    private static final String DIALOG_NAME_ACTION_PARAM_DIALOG = "actionParamDialog";

    public static Dialog createTaskExecutionControlPanel(Context context, View contentView) {
        final Dialog dialog = createModalDialog(context, "选择操作", contentView, (int) (ScreenUtil.getScreen(context).width * 0.3),
                (int) (ScreenUtil.getScreen(context).height * 0.3));
        getApplication(context).addDialog(DIALOG_NAME_TASK_EXECUTION_DIALOG, dialog);
        return dialog;
    }

    private static void dismissDialog(Context context, String name) {
        getApplication(context).removeDialog(name);
    }

    private static AutoTaskApplication getApplication(Context context) {
        return (AutoTaskApplication) context.getApplicationContext();
    }

    /**
     * 构造应用主界面对话框
     * @param context context
     * @return 对话框
     */
    public static Dialog createAppMainDialog(Context context) {
        final TaskMainView mainView = new TaskMainView(context);
        final Dialog dialog = createModalDialog(context, null, mainView, (int) (ScreenUtil.getScreen(context).width * 0.8),
                (int) (ScreenUtil.getScreen(context).height * 0.85));
        getApplication(context).addDialog(DIALOG_NAME_APP_MAIN, dialog);
        return dialog;
    }

    public static void dismissAppMainDialog(Context context) {
        dismissDialog(context, DIALOG_NAME_APP_MAIN);
    }

    /**
     * 构造task选择对话框
     * @param context context
//     * @param contentView content view
     * @return 对话框
     */
    public static Dialog createTaskDialog(Context context, Task currentTask) {
        final TaskInfoView taskInfoView = new TaskInfoView(context, currentTask);
        final Dialog dialog = createNoModalDialog(context, "新建TASK", taskInfoView, (int) (ScreenUtil.getScreen(context).width),
                (int) (ScreenUtil.getScreen(context).height));

        getApplication(context).addDialog(DIALOG_NAME_TASK_DIALOG, dialog);
        return dialog;
    }

    public static void dismissTaskDialog(Context context) {
        dismissDialog(context, DIALOG_NAME_TASK_DIALOG);
    }

    /**
     * 构造action选择对话框
     * @param context context
     * @return 对话框
     */
    public static Dialog createActionSelectionDialog(Context context, Pointer focusPointer, Task task) {
        final ActionSelectionView actionSelectionView = new ActionSelectionView(context,
                focusPointer, task);

        final Dialog dialog = createModalDialog(context, "选择ACTION", actionSelectionView, (int) (ScreenUtil.getScreen(context).width * 0.8),
                (int) (ScreenUtil.getScreen(context).height * 0.8));

        getApplication(context).addDialog(DIALOG_NAME_ACTION_SELECTION_DIALOG, dialog);
        return dialog;
    }

    public static void dismissActionSelectionDialog(Context context) {
        dismissDialog(context, DIALOG_NAME_ACTION_SELECTION_DIALOG);
    }

    /**
     * 构造action参数对话框
     * @param context context
     * @return action参数对话框
     */
    public static Dialog createActionParamDialog(Context context, Action action, Task task) {
        final ActionParamView paramView = new ActionParamView(context, action, task);

        final Dialog dialog = createModalDialog(context, "选择ACTION", paramView, (int) (ScreenUtil.getScreen(context).width * 0.8),
                (int) (ScreenUtil.getScreen(context).height * 0.5));

        getApplication(context).addDialog(DIALOG_NAME_ACTION_PARAM_DIALOG, dialog);
        return dialog;
    }

    public static void dismissActionParamDialog(Context context) {
        dismissDialog(context, DIALOG_NAME_ACTION_PARAM_DIALOG);
    }

    /**
     * 构造对话框
     * @param context context
     * @param title title
     * @param contentView 内容view
     * @param width 宽
     * @param height 高
     * @return 对话框
     */
    public static Dialog createNoModalDialog(Context context, String title, View contentView, int width, int height) {
        return createDialog(context, title, contentView, true, width, height);
    }

    /**
     * 构造对话框
     * @param context context
     * @param title title
     * @param contentView 内容view
     * @param width 宽
     * @param height 高
     * @return 对话框
     */
    public static Dialog createModalDialog(Context context, String title, View contentView, int width, int height) {
        return createDialog(context, title, contentView, false, width, height);
    }

    /**
     * 构造对话框
     * @param context context
     * @param title title
     * @param contentView 内容view
     * @param width 宽
     * @param height 高
     * @return 对话框
     */
    private static Dialog createDialog(Context context, String title, View contentView, boolean cancelable, int width, int height) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_TabAutoClickTest_NoActionBar);
        builder.setTitle(title);
        builder.setCancelable(cancelable);

        builder.setView(contentView);

        final Dialog dialog = builder.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0 TYPE_APPLICATION_OVERLAY
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.show();

        dialog.getWindow().setLayout(width, height);
        return dialog;
    }
}
