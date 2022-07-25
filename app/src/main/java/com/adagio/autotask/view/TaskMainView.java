package com.adagio.autotask.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.R;
import com.adagio.autotask.databinding.TaskMainLayoutBinding;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.task.service.TaskService;
import com.adagio.autotask.util.DialogUtil;
import com.adagio.autotask.util.ScreenUtil;
import com.adagio.autotask.view.adapter.TaskListAdapter;

import java.util.List;
import java.util.Optional;

/**
 * Task主界面
 */
public class TaskMainView extends LinearLayout {
    private final String TAG = getClass().getSimpleName();

    private TaskMainLayoutBinding binding;

    private TaskService taskService = AutoTaskApplication.getTaskService();

    public TaskMainView(Context context) {
        super(context);

        init();
    }

    private void init() {
        binding = TaskMainLayoutBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot());

        fillTaskListData();
        initEvent();
    }

    private void fillTaskListData() {
        // 查询task数据
        final List<Task> allTask = taskService.selectAllTask();//MainActivity.dbHelper.selectAllTask();
        final TaskListAdapter taskListAdapter = buildTaskListAdapter(allTask);
        binding.mainViewTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mainViewTaskList.setAdapter(taskListAdapter);
    }

    @NonNull
    private TaskListAdapter buildTaskListAdapter(List<Task> allTask) {
        final TaskListAdapter taskListAdapter = new TaskListAdapter(allTask);

        // 执行task
        taskListAdapter.setExecutionClickListener((task) -> {
            final Optional<Task> taskOptional = taskService.selectById(task.getId()); //taskService.selectByName(task.getTaskName());
            taskOptional.ifPresent(t -> {
                Log.e(TAG, "Changed======");
                DialogUtil.dismissAppMainDialog(getContext());

                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
                WindowManager manager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                // 设置window type
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                }
                // 设置图片格式，效果为背景透明
                params.format = PixelFormat.RGBA_8888;
                // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                // 调整悬浮窗显示的停靠位置为右侧底部
                params.gravity = Gravity.LEFT | Gravity.BOTTOM;
                // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
                params.x = 0;
                params.y = 0;
                // 设置悬浮窗口长宽数据
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            LayoutInflater inflater = LayoutInflater.from(getApplication());
//            // 获取浮动窗口视图所在布局
//            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.task_execution_control_panel_layout, null);

                final TaskExecutionControlPanelView executionControlPanelView = new TaskExecutionControlPanelView(getContext(), task);
                // 添加mFloatLayout
                manager.addView(executionControlPanelView, params);
                // 浮动窗口按钮
//            floatToucherView = layout.findViewById(R.id.go_mainhome);
                //UNSPECIFIED是未指定模式
                executionControlPanelView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                setTouchListener(executionControlPanelView, executionControlPanelView, params, manager);
            });

        });

        // 删除task
        taskListAdapter.setDeleteClickListener((task) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_TabAutoClickTest_NoActionBar);
            builder.setTitle("删除确认");
            builder.setMessage("是否要删除[" + task.getTaskName() + "]?");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", (dialogInterface, i) -> {
                taskService.deleteByTaskId(task.getId());
                fillTaskListData();
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

            Dialog dialog = builder.create();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0 TYPE_APPLICATION_OVERLAY
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
            wlp.gravity = Gravity.CENTER;
            dialog.show();
            dialog.getWindow().setLayout((int) (ScreenUtil.getScreen(getContext()).width * 0.75), 400);
        });

        // 编辑task
        taskListAdapter.setEditClickListener((this::addOrEditTask));
        return taskListAdapter;
    }

    private void setTouchListener(View view, View viewLayout,
                                  WindowManager.LayoutParams params,
                                  WindowManager windowManager) {
        view.setOnTouchListener(new View.OnTouchListener() {

            private float rawX;
            private float rawY;
            private boolean isMoving;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        viewLayout.setAlpha(1.0f);//设置其透明度
                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        isMoving = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        float distanceX = event.getRawX() - rawX;
                        float distanceY = event.getRawY() - rawY;
                        //mFloatView.getMeasuredWidth()和mFloatView.getMeasuredHeight()都是100
                        if (params.gravity == (Gravity.RIGHT|Gravity.BOTTOM)) {
                            params.x = (int) (params.x - distanceX);
                        } else if (params.gravity == (Gravity.LEFT|Gravity.BOTTOM)) {
                            params.x = (int) (params.x + distanceX);
                        }
                        params.y = (int) (params.y - distanceY);
                        // 刷新
                        windowManager.updateViewLayout(viewLayout, params);
                        rawX = event.getRawX();
                        rawY = event.getRawY();
                        isMoving = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        windowManager.updateViewLayout(viewLayout, params);
                        // 不是移动，响应点击事件
                        if (!isMoving) view.performClick();
                        break;
                }
                return true;
            }
        });
    }

    private void initEvent() {
        // 添加新任务事件
        binding.mainViewAddNewTaskBtn.setOnClickListener((view) -> addOrEditTask(new Task()));
        // 删除
        binding.mainViewCloseBtn.setOnClickListener(v -> {
            if (AutoTaskApplication.getScreenFocusView() != null) {
                AutoTaskApplication.getScreenFocusView().destroy();
            }
            DialogUtil.dismissAppMainDialog(getContext());
        });
    }

    /**
     * 新增或者修改task
     * @param currentTask task
     */
    private void addOrEditTask(Task currentTask) {
        DialogUtil.createTaskDialog(getContext(), currentTask);
    }

}
