package com.adagio.autotask.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.adagio.autotask.databinding.TaskExecutionControlPanelLayoutBinding;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.util.DialogUtil;

/**
 * Task执行控制界面
 */
public class TaskExecutionControlPanelView extends LinearLayout {
    private final String TAG = getClass().getSimpleName();

    private TaskExecutionControlPanelLayoutBinding binding;

    private final Task currentTask;

    public TaskExecutionControlPanelView(Context context, Task currentTask) {
        super(context);
        this.currentTask = currentTask;

        init();
    }

    private void init() {
        binding = TaskExecutionControlPanelLayoutBinding.inflate(LayoutInflater.from(getContext()));

        addView(binding.getRoot());
        initEvent();
    }

    private void initEvent() {
        // 执行状态
        currentTask.setStatusListener(status -> {
            post(() -> {
                binding.taskExecutionStatusView.setText(status.getName());
            });
        });
        // 执行次数
        currentTask.setExecutionTimesListener((taskExecutionTimes, actionExecutionTimes) -> {
            post(() -> {
                binding.taskExecutionTimesView.setText(taskExecutionTimes + "");
                binding.taskActionExecutionTimesView.setText(actionExecutionTimes + "");
            });
        });
        // 返回首页
        binding.taskExecutionHomeBtn.setOnClickListener(v -> {
            currentTask.stop();

            createMainAppDialog();
        });
        // 暂停
        binding.taskExecutionPauseBtn.setOnClickListener(v -> {
            currentTask.pause();
        });
        // 开始
        binding.taskExecutionPlayBtn.setOnClickListener(v -> {
            currentTask.start();
        });
        // 停止
        binding.taskExecutionStopBtn.setOnClickListener(v -> {
            currentTask.stop();

            createMainAppDialog();
        });
    }

    private void createMainAppDialog() {
        DialogUtil.createAppMainDialog(getContext());
        WindowManager manager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (isAttachedToWindow()) {
            manager.removeViewImmediate(this);
        }
    }

}
