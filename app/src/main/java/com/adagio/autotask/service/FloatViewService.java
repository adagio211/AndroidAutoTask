package com.adagio.autotask.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.adagio.autotask.R;
import com.adagio.autotask.util.DialogUtil;

public class FloatViewService extends Service {
    private final String TAG = getClass().getSimpleName();

    // 定义浮动窗口布局
    private LinearLayout mFloatLayout;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private TouchViewThinCountDownTimer touchViewThinCountDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        createToucherView();
        touchViewThinCountDownTimer = new TouchViewThinCountDownTimer(2500, 1000); //设置计时2.5s
        touchViewThinCountDownTimer.start();
    }

    private void createToucherView() {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        // 设置window type
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 调整悬浮窗显示的停靠位置为右侧底部
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.assistive_touch_layout, null);
        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        // 浮动窗口按钮
        View floatToucherView = mFloatLayout.findViewById(R.id.go_mainhome);
        //UNSPECIFIED是未指定模式
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        setTouchListener(floatToucherView, mFloatLayout, wmParams, mWindowManager);

        floatToucherView.setOnClickListener(v -> {
            // 构造对话框
            DialogUtil.createAppMainDialog(this);
        });
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
                        touchViewThinCountDownTimer.cancel();//取消计时
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
                        touchViewThinCountDownTimer.start();//重新开始计时
//                        if (wmParams.x < screenWidth / 2) {
//                            //在屏幕右侧
//                            wmParams.x = 0;
//                            wmParams.y = wmParams.y - 0;
//                        } else {
//                            wmParams.x = screenWidth;
//                            wmParams.y = wmParams.y - 0;
//                        }
                        windowManager.updateViewLayout(viewLayout, params);
                        // 不是移动，响应点击事件
                        if (!isMoving) view.performClick();
                        break;
                }
                return true;//此处必须返回false，否则OnClickListener获取不到监听
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class TouchViewThinCountDownTimer extends CountDownTimer {

        public TouchViewThinCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mFloatLayout.setAlpha(0.4f);
        }
    }
}
