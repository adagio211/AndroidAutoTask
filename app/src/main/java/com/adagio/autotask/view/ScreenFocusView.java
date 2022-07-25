package com.adagio.autotask.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.databinding.ScreenFocusLayoutBinding;
import com.adagio.autotask.motion.Pointer;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.util.DialogUtil;

/**
 * Task主界面
 */
public class ScreenFocusView extends LinearLayout {
    private final String TAG = getClass().getSimpleName();

    private final Task currentTask;

    private ScreenFocusLayoutBinding binding;

    private float pointerX;
    private float pointerY;

    private View focusLayoutView;

    public ScreenFocusView(Context context, Task currentTask) {
        super(context);
        this.currentTask = currentTask;

        init();
    }

    private void init() {
        createView();
    }

    private void createView() {
        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        final WindowManager windowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        binding = ScreenFocusLayoutBinding.inflate(LayoutInflater.from(getContext()));

        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // 设置window type
//        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        // 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为右侧底部
//        layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        layoutParams.gravity = Gravity.CENTER;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        layoutParams.x = 0;
        layoutParams.y = 0;
        // 设置悬浮窗口长宽数据
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // 添加mFloatLayout
        focusLayoutView = binding.getRoot();
        windowManager.addView(focusLayoutView, layoutParams);

        AutoTaskApplication.setScreenFocusView(this);

        // 浮动窗口按钮
        //UNSPECIFIED是未指定模式
        focusLayoutView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        setTouchListener(binding.actionFocusBtn, focusLayoutView, layoutParams, windowManager);

        binding.actionFocusBtn.setOnClickListener(v -> {
            // 构造对话框
            DialogUtil.createActionSelectionDialog(getContext(), new Pointer(pointerX, pointerY), currentTask);
        });
    }

    private void setTouchListener(View viewLayout, final View frameLayout,
                                  WindowManager.LayoutParams layoutParams,
                                  WindowManager windowManager) {
        viewLayout.setOnTouchListener(new OnTouchListener() {

            private float eventX;
            private float eventY;
            private boolean isMoving;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        eventX = event.getRawX();
                        eventY = event.getRawY();
                        ScreenFocusView.this.pointerX = eventX;
                        ScreenFocusView.this.pointerY = eventY;
                        isMoving = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        float distanceX = event.getRawX() - eventX;
                        float distanceY = event.getRawY() - eventY;
                        // gravity left,right是减
                        layoutParams.x = (int) (layoutParams.x + distanceX);
                        layoutParams.y = (int) (layoutParams.y + distanceY);

//                        final float xDistance = event.getX() - eventX;
//                        final float yDistance = event.getY() - eventY;
//                        if (xDistance != 0 && yDistance != 0) {
//                            int l = (int) (v.getLeft() + xDistance);
//                            int r = (int) (v.getRight() + xDistance);
//                            int t = (int) (v.getTop() + yDistance);
//                            int b = (int) (v.getBottom() + yDistance);
//                            frameLayout.layout(l, t, r, b);
//                            Log.e(TAG, "l====" + l);
//                            Log.e(TAG, "r====" + r);
//                            Log.e(TAG, "t====" + t);
//                            Log.e(TAG, "b====" + b);
//                        }
                        // 刷新
                        windowManager.updateViewLayout(frameLayout, layoutParams);
                        eventX = event.getRawX();
                        eventY = event.getRawY();
                        ScreenFocusView.this.pointerX = eventX;
                        ScreenFocusView.this.pointerY = eventY;
                        isMoving = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        windowManager.updateViewLayout(frameLayout, layoutParams);
                        ScreenFocusView.this.pointerX = eventX;
                        ScreenFocusView.this.pointerY = eventY;
                        // 不是移动，响应点击事件
                        if (!isMoving) viewLayout.performClick();
                        break;
                }
                return true;
            }
        });
    }

    public void destroy() {
        final WindowManager windowManager = (WindowManager) getContext().
                getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (focusLayoutView != null && focusLayoutView.isAttachedToWindow()) {
            windowManager.removeViewImmediate(focusLayoutView);
            AutoTaskApplication.setScreenFocusView(null);
        }
    }
}
