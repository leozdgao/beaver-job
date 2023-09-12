package me.leozdgao.beaver.spi;

import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

/**
 * 任务数据持久化服务
 * @author zhendong.gzd
 */
public interface TaskPersistenceCommandService {
    Task createTask(Task task);

    Task createTask(Task task, TaskStatus status);

    /**
     * 将任务设置为失败态
     * @param task 任务
     * @param msg 错误消息
     * @param cause 罪魁祸首
     */
    void taskFailed(Task task, String msg, Throwable cause);

    void updateTaskStatus(Task task, TaskStatus status);
}
