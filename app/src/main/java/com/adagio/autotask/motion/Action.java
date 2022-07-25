package com.adagio.autotask.motion;

/**
 * 动作
 */
public interface Action {

    final long DEFAULT_DELAY_MILL_SECONDS = 1000;
    final long DEFAULT_RANDOM_BEGIN = 1000L;
    final long DEFAULT_RANDOM_END = 5000L;

    /**
     * 获取id
     * @return id
     */
    Long getId();

    /**
     * 设置id
     * @param id id
     */
    void setId(Long id);

    /**
     * 获取action名称
     * @return 名称
     */
    String getName();

    /**
     * 设置名称
     * @param name 名称
     */
    void setName(String name);

    /**
     * 序号
     * @return 序号
     */
    int getOrder();

    /**
     * 设置序号
     * @param order 排序号
     * @return 返回当前对象
     */
    void setOrder(int order);

    /**
     * 动作类型
     * @return 类型
     */
    ActionType getActionType();

    /**
     * 持续时间
     * @return 持续时间,毫秒
     */
    long getDuration();

    /**
     * 设置执行时长,毫秒
     * @param duration 执行时长,毫秒
     */
    void setDuration(long duration);

    /**
     * 延迟时间
     * @return 延迟时间,毫秒
     */
    long getDelay();

    /**
     * 设置延迟时长,毫秒
     * @param delay 延迟时长,毫秒
     */
    void setDelay(long delay);

    /**
     * 执行动作
     */
    void execute();

    /**
     * 设置随机
     * @param beginDelay 随机起始
     * @param endDelay 随机结束值
     */
    void setRandomDelay(long beginDelay, long endDelay);

    /**
     * 关闭随机
     */
    void closeRandomDelay();

    /**
     * 是否是随机延迟
     * @return boolean
     */
    boolean isRandomDelay();

    /**
     * 随机起始
     * @return long
     */
    long getRandomDelayBegin();

    /**
     * 随机最大
     * @return long
     */
    long getRandomDelayEnd();
}
