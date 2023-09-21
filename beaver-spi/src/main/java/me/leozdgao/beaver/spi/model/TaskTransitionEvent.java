package me.leozdgao.beaver.spi.model;

public enum TaskTransitionEvent {
    /**
     * 计划
     */
    PLAN,
    /**
     * 重新安排
     */
    REARRANGE,
    /**
     * 分发
     */
    DISPATCH,
    /**
     * 分发失败
     */
    DISPATCH_FAIL,
    /**
     * 成功
     */
    SUCCESS,
    /**
     * 失败
     */
    FAIL,
    /**
     * 取消
     */
    CANCEL
}
