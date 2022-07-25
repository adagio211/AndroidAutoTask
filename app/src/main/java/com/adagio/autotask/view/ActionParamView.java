package com.adagio.autotask.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.adagio.autotask.databinding.ActionSelectParamLayoutBinding;
import com.adagio.autotask.motion.Action;
import com.adagio.autotask.task.model.Task;
import com.adagio.autotask.util.DialogUtil;

/**
 * Action参数界面
 */
public class ActionParamView extends LinearLayout {
    private final String TAG = ActionParamView.class.getSimpleName();

    private ActionSelectParamLayoutBinding binding;
    private final Action currentAction;
    private final Task currentTask;

    public ActionParamView(Context context, Action currentAction, Task currentTask) {
        super(context);
        this.currentAction = currentAction;
        this.currentTask = currentTask;

        init();
    }

    private void init() {
        binding = ActionSelectParamLayoutBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot());

        fillDataView(currentAction);
        initBtnEvent(currentAction);
    }

    private void fillDataView(Action currentAction) {
        binding.actionNameInput.setTag(currentAction.getId());
        binding.actionNameInput.setText(currentAction.getName());
        binding.actionDelayInput.setText(currentAction.getDelay() + "");
        binding.actionOrderInput.setText(currentAction.getOrder() + "");

        if (!currentAction.isRandomDelay()) {
            binding.actionRandomBeginInput.setVisibility(GONE);
            binding.actionRandomEndInput.setVisibility(GONE);
            binding.actionDelayInput.setVisibility(VISIBLE);
            binding.actionDelayInput.setText(currentAction.getDelay() + "");
            binding.actionRandomSwitch.setChecked(false);
        } else {
            binding.actionRandomBeginInput.setVisibility(VISIBLE);
            binding.actionRandomEndInput.setVisibility(VISIBLE);
            binding.actionDelayInput.setVisibility(GONE);
            setRandomTimeText(currentAction);
            binding.actionRandomSwitch.setChecked(true);
        }
    }

    private void setRandomTimeText(Action currentAction) {
        // 随机开始时间
        if (currentAction.getRandomDelayBegin() == 0L) {
            binding.actionRandomBeginInput.setText(Action.DEFAULT_RANDOM_BEGIN + "");
        } else {
            binding.actionRandomBeginInput.setText(currentAction.getRandomDelayBegin() + "");
        }
        // 随机结束时间
        if (currentAction.getRandomDelayEnd() == 0L) {
            binding.actionRandomEndInput.setText(Action.DEFAULT_RANDOM_END + "");
        } else {
            binding.actionRandomEndInput.setText(currentAction.getRandomDelayEnd() + "");
        }
    }

    private void initBtnEvent(Action currentAction) {
        // 保存
        binding.actionSelectParamSaveBtn.setOnClickListener(v -> {
            currentAction.setName(binding.actionNameInput.getText().toString());
            currentAction.setId(binding.actionNameInput.getTag() == null ? null : Long.parseLong(binding.actionNameInput.getTag().toString()));
            currentAction.setOrder(Integer.parseInt(binding.actionOrderInput.getText().toString()));
            final int actionOrder = currentTask.getNextActionOrder();
            if(currentAction.getOrder() < 0) currentAction.setOrder(actionOrder);

            if (!currentTask.getActions().contains(currentAction)) {
                currentTask.addAction(currentAction);
            }
            currentTask.reorderActions();

            // random
            if (binding.actionRandomSwitch.isChecked()) {
                currentAction.setRandomDelay(Long.parseLong(binding.actionRandomBeginInput.getText().toString()),
                        Long.parseLong(binding.actionRandomEndInput.getText().toString()));
            } else {
                currentAction.setDelay(Long.parseLong(binding.actionDelayInput.getText().toString()));
                currentAction.closeRandomDelay();
            }

            DialogUtil.dismissActionParamDialog(getContext());
            DialogUtil.createTaskDialog(getContext(), currentTask);
        });

        // 是否随机执行switch
        binding.actionRandomSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            if (!isChecked) {
                binding.actionRandomBeginInput.setVisibility(GONE);
                binding.actionRandomEndInput.setVisibility(GONE);
                binding.actionDelayInput.setVisibility(VISIBLE);
                binding.actionDelayInput.setText(currentAction.getDelay() + "");
            } else {
                binding.actionRandomBeginInput.setVisibility(VISIBLE);
                binding.actionRandomEndInput.setVisibility(VISIBLE);
                binding.actionDelayInput.setVisibility(GONE);

                setRandomTimeText(currentAction);
            }
        });

        binding.actionSelectParamCancelBtn.setOnClickListener(v -> DialogUtil.dismissActionParamDialog(getContext()));
    }

}
