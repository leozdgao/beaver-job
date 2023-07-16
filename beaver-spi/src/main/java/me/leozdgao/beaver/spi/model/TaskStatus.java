package me.leozdgao.beaver.spi.model;

/**
 * @author leozdgao
 */

public enum TaskStatus {
    /**
     * 等待
     */
    REQUESTING,
    /**
     * 已入队列
     */
    WAITING,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 执行成功
     */
    SUCCESS,
    /**
     * 执行失败
     */
    FAILED
}
