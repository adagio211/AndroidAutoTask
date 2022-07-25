package com.adagio.autotask.motion;

import com.adagio.autotask.util.UUIDUtil;

/**
 * TAP action
 */
public class TapAction implements Action {

    private final Pointer pointer;

    private final ActionType type = ActionType.TAP;
    private int order;
    private long duration = GestureSimulator.TAP_TIME;
    private long delay = DEFAULT_DELAY_MILL_SECONDS;
    private String name = type.getName();
    private boolean random;
    private long randomDelayBegin;
    private long randomDelayEnd;
    private Long id;

    public TapAction(Pointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public ActionType getActionType() {
        return type;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void execute() {
        GestureSimulator.instance().tap(pointer, duration);
    }

    @Override
    public void setRandomDelay(long beginDelay, long endDelay) {
        random = true;
        randomDelayBegin = beginDelay;
        randomDelayEnd = endDelay;
    }

    @Override
    public void closeRandomDelay() {
        random = false;
    }

    @Override
    public boolean isRandomDelay() {
        return random;
    }

    @Override
    public long getRandomDelayBegin() {
        return randomDelayBegin;
    }

    @Override
    public long getRandomDelayEnd() {
        return randomDelayEnd;
    }

}
