package com.adagio.autotask.view.adapter;

import static java.util.Objects.isNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adagio.autotask.R;
import com.adagio.autotask.task.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task list view item adapter
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();

    private List<Task> tasks;

    public TaskListAdapter(List<Task> tasks) {
        this.tasks = (tasks == null ? new ArrayList<Task>() : tasks).stream().filter(s -> !isNull(s)).collect(Collectors.toList());
    }

    private TaskListClickListener executionClickListener;
    private TaskListClickListener editClickListener;
    private TaskListClickListener deleteClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item_layout, parent, false);

        return new TaskListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        // 设置要显示的图片和文字
        holder.taskNameView.setText(task.getTaskName());
        holder.taskNameView.setTag(task.getId());
        holder.executionView.setOnClickListener(v -> executionClickListener.onClick(task));
        holder.editView.setOnClickListener(v -> editClickListener.onClick(task));
        holder.delView.setOnClickListener(v -> deleteClickListener.onClick(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final String TAG = getClass().getSimpleName();

        private final TextView taskNameView;
        private final ImageView executionView;
        private final ImageView editView;
        private final ImageView delView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNameView = itemView.findViewById(R.id.task_list_name_text_view);
            Log.e(TAG, "taskNameView=====" + taskNameView);
            executionView = itemView.findViewById(R.id.task_list_exec_view);
            editView= itemView.findViewById(R.id.task_list_edit_view);
            delView= itemView.findViewById(R.id.task_list_del_view);
        }
    }

    @FunctionalInterface
    public interface TaskListClickListener {
        void onClick(Task task);
    }

    public void setExecutionClickListener(TaskListClickListener executionClickListener) {
        this.executionClickListener = executionClickListener;
    }

    public void setEditClickListener(TaskListClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }

    public void setDeleteClickListener(TaskListClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }
}
