package com.adagio.autotask.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.adagio.autotask.R;
import com.adagio.autotask.databinding.ActionSelectLayoutBinding;
import com.adagio.autotask.motion.Action;
import com.adagio.autotask.motion.ActionType;
import com.adagio.autotask.motion.DownwardSwipeAction;
import com.adagio.autotask.motion.LeftSwipeAction;
import com.adagio.autotask.motion.LongTapAction;
import com.adagio.autotask.motion.Pointer;
import com.adagio.autotask.motion.RightSwipeAction;
import com.adagio.autotask.motion.TapAction;
import com.adagio.autotask.motion.UpwardSwipeAction;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.util.DialogUtil;

/**
 * 选择action主界面
 */
public class ActionSelectionView extends LinearLayout {
    private final String TAG = ActionSelectionView.class.getSimpleName();

    private ActionSelectLayoutBinding binding;
    private final Pointer pointer;
    private final Task currentTask;

    public ActionSelectionView(Context context, Pointer pointer, Task currentTask) {
        super(context);
        this.pointer = pointer;
        this.currentTask = currentTask;

        init();
    }

    private void init() {
        binding = ActionSelectLayoutBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot());

        initBtnEvent();
    }

    private void initBtnEvent() {
        // 取消
        binding.actionSelectCancelBtn.setOnClickListener(v -> {
            DialogUtil.dismissActionSelectionDialog(getContext());
            DialogUtil.createTaskDialog(getContext(), currentTask);
        });

        selectActionEvent(binding.actionAddTapBtn);
        selectActionEvent(binding.actionAddLongTapBtn);
        selectActionEvent(binding.actionAddLeftBtn);
        selectActionEvent(binding.actionAddRightBtn);
        selectActionEvent(binding.actionAddUpBtn);
        selectActionEvent(binding.actionAddDownBtn);
    }

    private void selectActionEvent(Button button) {
        button.setOnClickListener(buttonv -> {
            Action action;
            // tap
            if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_tap))) {
                action = new TapAction(pointer);
            } else if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_long_tap))) {
                action = new LongTapAction(pointer, ActionType.LONG_TAP.getName());
            // swipe left
            } else if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_swipe_left))) {
                action = new LeftSwipeAction(pointer);
            // swipe right
            } else if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_swipe_righ))) {
                action = new RightSwipeAction(pointer);
            // swipe up
            } else if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_swipe_up))) {
                action = new UpwardSwipeAction(pointer);
            // swipe down
            } else if (buttonv.getTag().equals(getContext().getString(R.string.tag_action_swipe_down))) {
                action = new DownwardSwipeAction(pointer);
            } else {
                action = new TapAction(pointer);
            }

//            if (action.getId() == null) action.setId(System.nanoTime());
            action.setOrder(currentTask.getNextActionOrder());
            createActionParamDialog(action);
            DialogUtil.dismissActionSelectionDialog(getContext());
        });
    }

    /**
     * 构造action参数弹窗
     * @param action action
     */
    private void createActionParamDialog(@NonNull Action action) {
        DialogUtil.createActionParamDialog(getContext(), action, currentTask);
    }

}
