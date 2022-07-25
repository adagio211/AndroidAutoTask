package com.adagio.autotask.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {

    public static Screen getScreen(Context context) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new Screen(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static class Screen {
        public final int width;
        public final int height;

        public Screen(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }
}
