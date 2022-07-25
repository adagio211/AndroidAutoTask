package com.adagio.autotask.util;

import android.content.Context;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

public class ToastUtil {

    public static void createToast(Context context, String message, int period) {
//        Toast.makeText(context.getApplicationContext(), message, period).show();

        ToastUtils.show(message);
//        Toast.ma

//        new XToast((AutoTaskApplication) context.getApplicationContext()).setContentView(R.layout.activity_main).setDuration(1000).
//                setAnimStyle(android.R.style.Animation_Translucent).
//                setBackgroundDimAmount(0.5f).
//                setText(android.R.id.message, message).show();
    }
}
