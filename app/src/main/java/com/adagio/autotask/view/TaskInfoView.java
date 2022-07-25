package com.adagio.autotask.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.databinding.TaskInfoLayoutBinding;
import com.adagio.autotask.motion.Action;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.task.service.TaskService;
import com.adagio.autotask.util.DialogUtil;
import com.adagio.autotask.util.ToastUtil;
import com.adagio.autotask.view.adapter.ActionListAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Task新增修改主界面
 */
public class TaskInfoView extends RelativeLayout {
    private final String TAG = getClass().getSimpleName();

    private TaskInfoLayoutBinding binding;

    private final Task currentTask;

    private final TaskService taskService = AutoTaskApplication.getTaskService();

    public TaskInfoView(Context context, Task currentTask) {
        super(context);
        this.currentTask = currentTask;

        init();
    }

    private void init() {
        binding = TaskInfoLayoutBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot());

        fillViewData(currentTask);
        initEvent();
    }

    /**
     * 填充界面
     * @param task task
     */
    private void fillViewData(Task task) {
        binding.taskNameInput.setText(task.getTaskName());
        binding.taskNameInput.setTag(task.getId());
        binding.taskDescribeInput.setText(task.getTaskDescription());
        binding.taskAlwaysExecutionSwitch.setChecked(task.getIsAlwaysExecution() == 1);
        if (binding.taskAlwaysExecutionSwitch.isChecked()) {
            binding.taskExecutionTimesInput.setVisibility(GONE);
        } else {
            binding.taskExecutionTimesInput.setText(task.getExecutionTimes() + "");
            binding.taskExecutionTimesInput.setVisibility(VISIBLE);
        }

        final ActionListAdapter actionListAdapter = buildActionListAdapter(task.getActions());
        binding.taskActionListView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.taskActionListView.setAdapter(actionListAdapter);
    }

    /**
     * 刷新action列表
     * @param task task
     */
    private void refreshActionList(Task task) {
        final ActionListAdapter actionListAdapter = buildActionListAdapter(task.getActions());
        binding.taskActionListView.setAdapter(actionListAdapter);
    }

    /**
     * 构造action list adapter
     * @param allTaskAction action列表
     * @return ActionListAdapter
     */
    @NonNull
    private ActionListAdapter buildActionListAdapter(List<Action> allTaskAction) {
        ActionListAdapter actionListAdapter = new ActionListAdapter(allTaskAction);
        actionListAdapter.setDeleteClickListener(action -> {
            currentTask.removeAction(action);
            currentTask.reorderActions();
            refreshActionList(currentTask);
        });
        actionListAdapter.setEditClickListener(action -> {
            DialogUtil.createActionParamDialog(getContext(), action, currentTask);
        });
        return actionListAdapter;
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        // Cancel按钮
        binding.taskCancelBtn.setOnClickListener(v -> {
            DialogUtil.dismissTaskDialog(getContext());
            DialogUtil.createAppMainDialog(getContext());
        });

        // Save按钮
        binding.taskSaveBtn.setOnClickListener(v -> {
            fillTaskData(currentTask);
            if (currentTask.getTaskName() == null || currentTask.getTaskName().trim().equals("")) {
                ToastUtil.createToast(getContext(), "任务名称必须填写", Toast.LENGTH_LONG);
                return;
            }
            if (currentTask.getId() == null && taskService.selectCountTask(currentTask.getTaskName()) > 0) {
                ToastUtil.createToast(getContext(), "任务名称[" + currentTask.getTaskName() + "]重复", Toast.LENGTH_LONG);
                return;
            }
            // 一直执行
            if (currentTask.getIsAlwaysExecution() != 1) {
                if (currentTask.getExecutionTimes() == 0) {
                    ToastUtil.createToast(getContext(), "执行次数必填或者是选择一直执行", Toast.LENGTH_LONG);
                    return;
                }
            }

            // 设置action id
            currentTask.getActions().forEach(a -> {
                if (a.getId() == null) {
                    a.setId(System.nanoTime());
                }
            });

            taskService.saveOrUpdateTask(currentTask);
            DialogUtil.dismissTaskDialog(getContext());
            DialogUtil.createAppMainDialog(getContext());
            if (AutoTaskApplication.getScreenFocusView() != null) {
                AutoTaskApplication.getScreenFocusView().destroy();//.des
            }
        });

        // 是否一直执行switch
        binding.taskAlwaysExecutionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding.taskAlwaysExecutionSwitch.isChecked()) {
                binding.taskExecutionTimesInput.setVisibility(GONE);
            } else {
                binding.taskExecutionTimesInput.setVisibility(VISIBLE);
            }
        });

        // 添加新的action
        binding.taskAddNewActionBtn.setOnClickListener(v -> {
            fillTaskData(currentTask);

            // 选择新的action弹框
            if (AutoTaskApplication.getScreenFocusView() == null) {
                AutoTaskApplication.setScreenFocusView(new ScreenFocusView(getContext(), currentTask));
            }
            DialogUtil.dismissTaskDialog(getContext());
            DialogUtil.dismissAppMainDialog(getContext());
        });
    }

    /**
     * 根据UI填充task数据
     * @param task task
     */
    private void fillTaskData(Task task) {
        task.setTaskName(binding.taskNameInput.getText().toString());
        task.setId(Objects.isNull(binding.taskNameInput.getTag()) ? null : Integer.parseInt(String.valueOf(binding.taskNameInput.getTag())));
        task.setTaskDescription(binding.taskDescribeInput.getText().toString());
        // 一直执行
        if (binding.taskAlwaysExecutionSwitch.isChecked()) {
            task.setIsAlwaysExecution((short) 1);
        } else {
            task.setIsAlwaysExecution((short) 0);
            String executionTimesStr = binding.taskExecutionTimesInput.getText().toString();
            task.setExecutionTimes(Integer.parseInt(executionTimesStr.trim().equals("") ? "0" : executionTimesStr.trim()));
        }
    }

}
