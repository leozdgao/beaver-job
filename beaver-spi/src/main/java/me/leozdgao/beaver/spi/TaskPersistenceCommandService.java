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

    void updateTaskStatus(Task task, TaskStatus status);
}
