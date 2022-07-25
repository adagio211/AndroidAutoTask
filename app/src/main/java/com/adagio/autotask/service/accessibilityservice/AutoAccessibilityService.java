package com.adagio.autotask.service.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Region;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.adagio.autotask.R;
import com.adagio.autotask.motion.GestureSimulator;

/**
 * 無障礙服務
 */
public class AutoAccessibilityService extends AccessibilityService {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //服务开启时，调用
        //setServiceInfo();这个方法同样可以实现xml中的配置信息
        //可以做一些开启后的操作比如点两下返回

//        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
//        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
//                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                | AccessibilityEvent.TYPE_VIEW_CLICKED
//                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//                | AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
//        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        accessibilityServiceInfo.notificationTimeout = 0;
//        accessibilityServiceInfo.flags = AccessibilityServiceInfo.DEFAULT;
//        accessibilityServiceInfo.packageNames = new String[]{"com.shark.nougat"};
//        setServiceInfo(accessibilityServiceInfo);

        // 初始化手势模拟器
        GestureSimulator.init(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //关闭服务时,调用
        //如果有资源记得释放
        return super.onUnbind(intent);
    }

//    @Override
//    public boolean onGesture(@NonNull AccessibilityGestureEvent gestureEvent) {
//        return super.onGesture(gestureEvent);
//    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //这个方法是我们用的最多的方法，我们会在这个方法里写大量的逻辑操作。
        //通过对event的判断执行不同的操作
        //当窗口发生的事件是我们配置监听的事件时,会回调此方法.会被调用多次

        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        switch (event.getEventType()) {
            /**
             * AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED，
             * 而会调用AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED，
             * 而AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED只要内容改变后都会调用，
             * 所以一般是使用AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED来作为监测事件的
             */
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.i(TAG, "TYPE_WINDOW_STATE_CHANGED 界面改变");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                final AccessibilityNodeInfo nodeInfo = event.getSource();
//                Log.i(TAG, "TYPE_WINDOW_STATE_CHANGED 界面改变====" + nodeInfo.getViewIdResourceName());
//                if (nodeInfo.getText().equals(getString(R.string.execute))) {
//                    final List<Pointer> pointers = MainActivity.dbHelper.selectAllData();
//                    Toast.makeText(AutoAccessibilityService.this, "Data size [" + pointers.size() + "]",
//                            Toast.LENGTH_LONG).show();
//
//                    final Handler handler = new Handler();
//                    for (int i = 1; i <= pointers.size(); i++) {
//                        final Pointer p = pointers.get(i - 1);
//                        handler.postDelayed(() -> {
////                            gestureSimulator.tap(p.getX(), p.getY());
//                            gestureSimulator.swipeLeft(p);
//                        }, i * 1000);
//                    }

//                    final Optional<Task> task = MainActivity.dbHelper.selectByName("aaa");
//                    task.ifPresent(t -> {
//                        Log.e(TAG, JsonUtil.toJson(task));
//                        t.execute(this);
//                    });
//                }

//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);

//                final List<AccessibilityNodeInfo> nodeInfoList = event.getSource().findAccessibilityNodeInfosByText("CLICK");
//                nodeInfoList.get(0).per
//                Log.e(TAG, nodeInfo.getViewIdResourceName());
                Log.i(TAG, "TYPE_VIEW_CLICKED view被点击");
                this.disableSelf();
                break;
//            default:
//            final List<AccessibilityNodeInfo> nodeInfoList = event.getSource().findAccessibilityNodeInfosByText("CLICK");
////                nodeInfoList.get(0).per
//            Log.e(TAG, nodeInfoList.get(0).getViewIdResourceName());
//            Log.i(TAG, "TYPE_VIEW_CLICKED view被点击");

        }



//        String pkgName = event.getPackageName().toString();
//        String className = event.getClassName().toString();

//        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    @Override
    public void onInterrupt() {
        //当服务要被中断时调用.会被调用多次
    }

//    private AccessibilityNodeInfo getRootNodeInfo() {
//        AccessibilityEvent curEvent = mAccessibilityEvent;
//        AccessibilityNodeInfo nodeInfo = null;
//        if (Build.VERSION.SDK_INT >= 16) {
//            // 建议使用getRootInActiveWindow，这样不依赖当前的事件类型
//            if (mAccessibilityService != null) {
//                nodeInfo = mAccessibilityService.getRootInActiveWindow();
//                Log.d(TAG, "getRootNodeInfo: " + nodeInfo);
//            }
//            // 下面这个必须依赖当前的AccessibilityEvent
////            nodeInfo = curEvent.getSource();
//        } else {
//            nodeInfo = curEvent.getSource();
//        }
//        return nodeInfo;
//    }

}
