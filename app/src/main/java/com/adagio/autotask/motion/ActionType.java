package com.adagio.autotask.motion;

import com.adagio.autotask.R;

public enum ActionType {

    LONG_TAP(LongTapAction.class, "长按", R.drawable.ic_gesture_1f_long_tap),
    DOWN_SWIPE(DownwardSwipeAction.class, "下滑动", R.drawable.ic_gesture_1f_swipe_down),
    LEFT_SWIPE(LeftSwipeAction.class, "左滑动", R.drawable.ic_gesture_1f_swipe_left),
    RIGHT_SWIPE(RightSwipeAction.class, "右滑动", R.drawable.ic_gesture_1f_swipe_right),
    UP_SWIPE(UpwardSwipeAction.class, "上滑动", R.drawable.ic_gesture_1f_swipe_up),
    TAP(TapAction.class, "点击", R.drawable.ic_gesture_tap);

    private final Class<? extends Action> typeClass;
    private final String name;
    private final int icon;

    ActionType(Class<? extends Action> typeClass, String name, int icon) {
        this.typeClass = typeClass;
        this.name = name;
        this.icon = icon;
    }

    public Class<? extends Action> getTypeClass() {
        return typeClass;
    };
    public String getName() {
        return name;
    }
    public int getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return typeClass.getSimpleName();
    }
}
