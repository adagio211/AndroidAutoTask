package com.adagio.autotask.util;

import android.content.Context;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

public class ToastUtil {

    public static void createToast(String message) {
//        Toast.makeText(context.getApplicationContext(), message, period).show();
        ToastUtils.show(message);
    }
}
