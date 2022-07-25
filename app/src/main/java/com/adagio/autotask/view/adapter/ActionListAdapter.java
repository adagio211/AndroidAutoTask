package com.adagio.autotask.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adagio.autotask.R;
import com.adagio.autotask.motion.Action;

import java.util.List;

/**
 * Task list view item adapter
 */
public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ViewHolder> {
    private List<Action> actions;
    private ActionListClickListener deleteClickListener;
    private ActionListClickListener editClickListener;

    public ActionListAdapter(List<Action> actions) {
        this.actions = actions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_list_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Action action = actions.get(position);

        holder.actionTypeImageView.setImageResource(action.getActionType().getIcon());
        holder.actionNameView.setText(action.getOrder() + "." + action.getActionType().getName());
        holder.actionNameView.setTag(action.getId());
        holder.actionDelayView.setText(action.getDelay() + " 毫秒");

        holder.delView.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onClick(action);
            }
        });

        holder.editView.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onClick(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView actionTypeImageView;
        private final TextView actionNameView;
        private final TextView actionDelayView;
//        private final ImageView executionView;
        private final ImageView editView;
        private final ImageView delView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            actionTypeImageView = itemView.findViewById(R.id.action_list_action_type_icon_view);
            actionNameView = itemView.findViewById(R.id.action_list_name_text_view);
            actionDelayView = itemView.findViewById(R.id.action_list_delay_text_view);
//            executionView = itemView.findViewById(R.id.action_list_exec_view);
            editView = itemView.findViewById(R.id.action_list_edit_view);
            delView = itemView.findViewById(R.id.action_list_del_view);
        }
    }

    @FunctionalInterface
    public interface ActionListClickListener {
        void onClick(Action action);
    }

    public void setDeleteClickListener(ActionListClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public void setEditClickListener(ActionListClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }
}
