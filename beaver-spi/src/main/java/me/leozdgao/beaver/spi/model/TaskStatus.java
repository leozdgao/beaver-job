package me.leozdgao.beaver.spi.model;

import lombok.Getter;

/**
 * @author leozdgao
 */
@Getter
public enum TaskStatus {
    /**
     * 等待
     */
    REQUESTING(0),
    /**
     * 已入队列
     */
    WAITING(100),
    /**
     * 运行中
     */
    RUNNING(101),
    /**
     * 执行成功
     */
    SUCCESS(200),
    /**
     * 执行失败
     */
    FAILED(500),
    /**
     * 已取消
     */
    CANCEL(400)
    ;

    private final int code;

    TaskStatus(int code) {
        this.code = code;
    }

    public static TaskStatus of(Integer code) {
        if (code == null) {
            return null;
        }

        for (TaskStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }

        return null;
    }
}
