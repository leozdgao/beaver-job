package me.leozdgao.beaver.infrastructure;

import jakarta.inject.Inject;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

/**
 * 基于单库单表的任务持久化实现
 * @author zhendong.gzd
 */
public class TaskSinglePersistenceServiceImpl implements TaskPersistenceCommandService {
    private TaskMapper taskMapper;

    @Inject
    public TaskSinglePersistenceServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public Task createTask(Task task) {
        return createTask(task, TaskStatus.REQUESTING);
    }

    @Override
    public Task createTask(Task task, TaskStatus status) {
        // TODO: 雪花算法生成 ID
        System.out.printf("Create task: %s, status %s\n", task, status);

        // taskMapper.createTask();

        return task;
    }

    @Override
    public void updateTaskStatus(Task task, TaskStatus status) {
        System.out.printf("Update task: %s, status %s\n", task, status);
    }
}
