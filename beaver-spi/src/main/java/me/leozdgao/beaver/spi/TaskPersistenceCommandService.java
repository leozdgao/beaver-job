package me.leozdgao.beaver.spi;

import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskTransitionEvent;
import me.leozdgao.beaver.spi.model.TaskStatus;

import java.util.Map;

/**
 * 任务数据持久化服务
 * @author zhendong.gzd
 */
public interface TaskPersistenceCommandService {
    Task createTask(Task task);

    Task createTask(Task task, TaskStatus status);

    /**
     * 将任务设置为成功态
     * @param task 任务
     * @param result 结果
     */
    void taskSuccess(Task task, Map<String, Object> result);

    /**
     * 将任务设置为失败态
     * @param task 任务
     * @param cause 罪魁祸首
     */
    void taskFailed(Task task, Throwable cause);

    /**
     * 更新任务状态
     * @param task 任务
     * @param status 目标状态
     */
    void updateTaskStatus(Task task, TaskTransitionEvent status);
}
