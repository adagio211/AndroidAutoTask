package com.adagio.autotask.motion;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

import com.adagio.autotask.R;
import com.adagio.autotask.anim.ViewSizeChangeAnimation;
import com.adagio.autotask.databinding.LineLayoutBinding;
import com.adagio.autotask.util.ScreenUtil;

/**
 * 手势模拟器
 */
public class GestureSimulator {

    /** 滑动时间 */
    public static final long SWIPE_TIME = 1500L;
    /**  长按时间 */
    public static final long LONG_TAP_TIME = 500L;
    /** 短按时间 */
    public static final long TAP_TIME = 1L;
    /** 立即执行时间 */
    public static final long IMMEDIATE_EXECUTION_TIME = 100L;

    private final AccessibilityService accessibilityService;

    /** 屏幕宽度 */
    private final int screenWidth;
    /** 屏幕高度 */
    private final int screenHeight;

    private static GestureSimulator instance;

    public static GestureSimulator instance() {
        return instance;
    }

    public static GestureSimulator init(AccessibilityService accessibilityService) {
        if (instance != null) throw new RuntimeException("GestureSimulator must init once");
        instance = new GestureSimulator(accessibilityService);
        return instance;
    }

    private GestureSimulator(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
        final ScreenUtil.Screen screen = ScreenUtil.getScreen(accessibilityService);
        this.screenWidth = screen.width;
        this.screenHeight = screen.height;
    }

    public void tap(Pointer pointer, long duration) {
        tapClick(pointer.x, pointer.y, IMMEDIATE_EXECUTION_TIME, duration);
    }

    public void longTap(Pointer pointer, long duration) {
        tapClick(pointer.x, pointer.y, IMMEDIATE_EXECUTION_TIME, duration);
    }

    public void swipeUp(Pointer pointer, long duration) {
        swipe(pointer.x, pointer.y, pointer.x,
                getPositiveCoordinate(pointer.y, getAHalfScreeHeight()),
                IMMEDIATE_EXECUTION_TIME, duration);
    }

    public void swipeDown(Pointer pointer, long duration) {
        swipe(pointer.x, pointer.y, pointer.x,
                pointer.y + getAHalfScreeHeight(),
                IMMEDIATE_EXECUTION_TIME, duration);
    }

    public void swipeLeft(Pointer pointer, long duration) {
        swipe(pointer.x, pointer.y,
                getPositiveCoordinate(pointer.x, getAHalfScreenWidth()), pointer.y,
                IMMEDIATE_EXECUTION_TIME, duration);
    }

    public void swipeRight(Pointer pointer, long duration) {
        swipe(pointer.x, pointer.y,
                pointer.x + getAHalfScreenWidth(), pointer.y,
                IMMEDIATE_EXECUTION_TIME, duration);
    }

    private float getPositiveCoordinate(float coordinate, float offset) {
        return Math.max(coordinate - offset, 0.0f);
    }

    private float getAHalfScreeHeight() {
        return this.screenHeight / 1.0f;
    }

    private float getAHalfScreenWidth() {
        return this.screenWidth / 1.0f;
    }

    /**
     * 模拟点击事件
     * @param xCoordinate x坐标
     * @param yCoordinate y坐标
     */
    private void tapClick(float xCoordinate, float yCoordinate, long startTime, long clickTime) {
        Log.e("Tag","模拟点击事件");

        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(xCoordinate , yCoordinate);
        // 如果是单击的话clickTime就设为1,长按就设为500，单位是毫秒
//        final long clickTime = 1l;
        builder.addStroke(new GestureDescription.StrokeDescription(p, startTime, clickTime));
        GestureDescription gesture = builder.build();
        accessibilityService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e("Tag", "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e("Tag", "onCompleted: 取消..........");
            }
        }, null);
    }

    /**
     * 模拟滑动事件
     * @param startXCoordinate 起始x坐标
     * @param startYCoordinate 起始y坐标
     * @param endXCoordinate 结束x坐标
     * @param endYCoordinate 结束y坐标
     * @param startTime 0即可执行
     * @param duration  滑动时长
     */
    private void swipe(float startXCoordinate, float startYCoordinate, float endXCoordinate, float endYCoordinate,
                       final long startTime, final long duration) {
        Log.e("Tag","模拟滑动事件");

        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(startXCoordinate , startYCoordinate);
        p.lineTo(endXCoordinate , endYCoordinate);

//        final View lineView = LineLayoutBinding.inflate(LayoutInflater.from(accessibilityService.getApplicationContext())).getRoot();
//        final WindowManager manager = drawGestureOnTheScreen(lineView,
//                startXCoordinate, startYCoordinate, endXCoordinate, endYCoordinate,
//                duration);

        builder.addStroke(new GestureDescription.StrokeDescription(p, startTime, duration));
        GestureDescription gesture = builder.build();
        accessibilityService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
//                if(lineView.isAttachedToWindow()) {
//                    manager.removeViewImmediate(lineView);
//                }
                Log.e("Tag", "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
//                if(lineView.isAttachedToWindow()) {
//                    manager.removeViewImmediate(lineView);
//                }
                Log.e("Tag", "onCompleted: 取消..........");
            }
        }, null);
    }

    /**
     * 绘制手势动画
     * @param gestureView 手势动画
     */
    private WindowManager drawGestureOnTheScreen(View gestureView,
                                                 float startXCoordinate, float startYCoordinate, float endXCoordinate, float endYCoordinate,
                                                 long duration) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        WindowManager manager = (WindowManager) accessibilityService.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置window type
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.TRANSLUCENT;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为右侧底部
//        params.gravity = Gravity.;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity

//        params.x = (int) startXCoordinate;
//        params.y = (int) startYCoordinate;

        params.gravity = Gravity.CENTER_VERTICAL;
        params.x = 0;
        params.y = 0;

        // 设置悬浮窗口长宽数据
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            manager.addView(gestureView, params);

            handler.post(() -> {

//                Animation animation = new ViewSizeChangeAnimation(gestureView.findViewById(R.id.gesture_line_view), 5, 400);
                Animation animation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.0f);;
                animation.setDuration(duration);
                gestureView.findViewById(R.id.gesture_line_view).startAnimation(animation);
            });
        });
        return manager;
    }
}
