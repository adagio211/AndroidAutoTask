package com.adagio.autotask.task.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.adagio.autotask.AutoTaskApplication;
import com.adagio.autotask.motion.Action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * 任务,一系列action集合
 */
public class Task {
    private final String TAG = getClass().getSimpleName();

    private final ScheduledExecutorService actionExecutionPool = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService timeListenerPool = Executors.newSingleThreadExecutor();
    private final ExecutorService statusListenerPool = Executors.newSingleThreadExecutor();

    /** id */
    private Integer id;
    /** 任务名称 */
    private String taskName;
    /** 任务描述 */
    private String taskDescription;
    /** 执行次数 */
    private int executionTimes = 1;
    /** 是否是一直执行 */
    private short isAlwaysExecution = 0;
    /** Create date */
    private Date createDate;
    /** Update date */
    private Date updateDate;
    /** 动作集合 */
    private List<Action> actions = new ArrayList<>();

    /** 当前执行的次数，如果没有开始则是0 */
    private final AtomicInteger currentExecutionTimes = new AtomicInteger(0);
    /** 当前执行的action位置，如果没有开始则是0 */
    private final AtomicInteger currentExecutionActionPosition = new AtomicInteger(0);
    /** 当前状态 */
    private final AtomicReference<TaskStatus> status = new AtomicReference<>(TaskStatus.NOT_START);

    private StatusListener statusListener;
    private ExecutionTimesListener executionTimesListener;

    public Task() {
    }

    public Task(int id, String taskName, String taskDescription, List<Action> actions) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.actions = actions;
    }

    public Task(String taskName, String taskDescription, List<Action> actions) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.actions = actions;
    }

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setExecutionTimesListener(ExecutionTimesListener executionTimesListener) {
        this.executionTimesListener = executionTimesListener;
    }

    public synchronized void start() {
        if (actions == null || actions.size() == 0) {
            return;
        }

        if (isStarted()) return;
        actions.sort(Comparator.comparingInt(Action::getOrder));

        setStatus(TaskStatus.IN_EXECUTION);

        AutoTaskApplication.TASK_EXECUTION_POOL.execute(() -> {
            // 执行次数
            if (getIsAlwaysExecution() == 0) {
                executeLimitedTask();
            } else {
                executeUnlimitedTask();
            }
        });
    }

    /**
     * 刷新状态和执行次数
     * @return 是否要继续执行
     */
    private boolean refreshStatusExecutionTimes() {
        // 执行暂停
        if (isPaused()) {
            setStatus(TaskStatus.PAUSED);
            return false;
        }
        // 执行停止
        if (isStopped()) {
            incrementExecutionTimes(currentExecutionTimes,true);
            setStatus(TaskStatus.STOPPED);
            incrementExecutionTimes(currentExecutionActionPosition, true);
//            currentExecutionActionPosition.set(0);
            incrementExecutionTimes(currentExecutionTimes, true);
            return false;
        }
        // 执行完成
        if (isFinished()) {
            setStatus(TaskStatus.FINISHED);
//            currentExecutionActionPosition.set(0);
            incrementExecutionTimes(currentExecutionActionPosition, true);
            return false;
        }
        // 执行错误
        if (isError()) {
            setStatus(TaskStatus.ERROR);
            return false;
        }
        return true;
    }

    /**
     * 执行不限次数的任务
     */
    private void executeUnlimitedTask() {
        while(true) {
            if (!refreshStatusExecutionTimes()) return;

            executeOnce();
            incrementExecutionTimes(currentExecutionTimes, false);
        }
    }

    /**
     * 执行有限次数的任务
     */
    private void executeLimitedTask() {
        IntStream.of(executionTimes).forEach(i -> {
            if (!refreshStatusExecutionTimes()) return;

            executeOnce();
//            Log.e(TAG, "Execution current times ====" + currentExecutionTimes.get());
            incrementExecutionTimes(currentExecutionTimes, false);
        });
        // 执行结束
        setStatus(TaskStatus.FINISHED);
    }

    /**
     * 执行一次
     */
    private void executeOnce() {
        if (!refreshStatusExecutionTimes()) return;

        for (int i = currentExecutionActionPosition.get(); i < getActions().size(); i++) {
            final Action action = getActions().get(i);

            long totalDelay = 0L;
            if (action.isRandomDelay()) {
                final long actionRandomDelay =
                        ThreadLocalRandom.current().nextLong(action.getRandomDelayBegin(), action.getRandomDelayEnd());
                totalDelay += actionRandomDelay + action.getDuration();
            } else {
                totalDelay += action.getDelay() + action.getDuration();
            }
            Log.e(TAG, "Total delay=====" + totalDelay);
            try {
                final Long begin = System.currentTimeMillis();
                actionExecutionPool.schedule(action::execute, totalDelay, TimeUnit.MILLISECONDS).get();
                Log.e(TAG, "Execute time===" + (System.currentTimeMillis() - begin));
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                setStatus(TaskStatus.ERROR);
            }
            incrementExecutionTimes(currentExecutionActionPosition, false);
        }
        incrementExecutionTimes(currentExecutionActionPosition, true);
    }

    private void incrementExecutionTimes(AtomicInteger times, boolean isResetToZero) {
        if (isResetToZero) {
            times.set(0);
        } else {
            times.incrementAndGet();
        }

        if (executionTimesListener != null) {
            timeListenerPool.execute(() ->
                    executionTimesListener.onExecutionTimesChanged(currentExecutionTimes.get(),
                            currentExecutionActionPosition.get()));
        }
    }

    private void setStatus(TaskStatus status) {
        this.status.set(status);
        Log.e(TAG, "Set paused====" + status.getName());

        if (statusListener != null) {
            statusListenerPool.execute(() -> statusListener.onStatusChanged(status));
        }
    }

    private TaskStatus getStatus() {
        return this.status.get();
    }

    public void pause() {
        status.set(TaskStatus.PAUSED);
    }

    public void stop() {
        status.set(TaskStatus.STOPPED);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public List<Action> getActions() {
        return actions == null ? new ArrayList<>() : actions;
    }

    public int getNextActionOrder() {
        return getActions().stream().mapToInt(Action::getOrder).max().orElse(0) + 1;
    }

    public void addAction(Action action) {
        this.getActions().add(action);
    }

    public void removeAction(Action action) {
        this.getActions().removeIf(a -> Objects.equals(a.getId(), action.getId()));
    }

    public void reorderActions() {
        getActions().sort(Comparator.comparing(Action::getOrder));
        AtomicInteger order = new AtomicInteger(1);
        getActions().forEach(c -> c.setOrder(order.getAndIncrement()));
    }

    public int getExecutionTimes() {
        return executionTimes;
    }

    public void setExecutionTimes(int executionTimes) {
        this.executionTimes = executionTimes;
    }

    public short getIsAlwaysExecution() {
        return isAlwaysExecution;
    }

    public void setIsAlwaysExecution(short isAlwaysExecution) {
        this.isAlwaysExecution = isAlwaysExecution;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getCurrentExecutionTimes() {
        return currentExecutionTimes.get();
    }

    public boolean isStarted() {
        return status.get() == TaskStatus.IN_EXECUTION;
    }

    public boolean isStopped() {
        return status.get() == TaskStatus.STOPPED;
    }

    public boolean isFinished() {
        return status.get() == TaskStatus.FINISHED;
    }

    public boolean isPaused() {
        return status.get() == TaskStatus.PAUSED;
    }

    public boolean isError() {
        return status.get() == TaskStatus.ERROR;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", executionTimes=" + executionTimes +
                ", isAlwaysExecution=" + isAlwaysExecution +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", currentExecutionTimes=" + currentExecutionTimes +
                ", started=" + isStarted() +
                ", stopped=" + isStopped() +
                ", paused=" + isPaused() +
                ", finished=" + isFinished() +
                ", actions=" + actions +
                ", hashCode=" + this.hashCode() +
                '}';
    }

    public enum TaskStatus {
        NOT_START(0, "未开始"),
        IN_EXECUTION(1, "执行中"),
        PAUSED(2, "暂停"),
        STOPPED(3, "已停止"),
        STOPPING(4, "停止中"),
        FINISHED(5, "执行完成"),
        ABNORMAL(6, "取消"),
        ERROR(7, "错误");

        private final int status;
        private final String name;

        TaskStatus(int status, String name) {
            this.status = status;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @FunctionalInterface
    public interface StatusListener {
        void onStatusChanged(TaskStatus status);
    }

    @FunctionalInterface
    public interface ExecutionTimesListener {
        void onExecutionTimesChanged(long executionTimes, int actionExecutionPosition);
    }


}
