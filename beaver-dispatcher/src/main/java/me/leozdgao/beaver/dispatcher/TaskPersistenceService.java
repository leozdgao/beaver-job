package me.leozdgao.beaver.dispatcher;

/**
 * 任务数据持久化服务
 * @author zhendong.gzd
 */
public interface TaskPersistenceService {
    void createTask(Task task);

    void createTask(Task task, TaskStatus status);

    void updateTaskStatus(TaskStatus status);
}
