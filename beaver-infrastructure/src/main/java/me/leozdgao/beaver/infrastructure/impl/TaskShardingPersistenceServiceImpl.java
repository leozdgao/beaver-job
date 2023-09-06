package me.leozdgao.beaver.infrastructure.impl;

import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

/**
 * 基于分库分表的任务持久化实现
 * @author zhendong.gzd
 */
public class TaskShardingPersistenceServiceImpl implements TaskPersistenceCommandService {
    @Override
    public Task createTask(Task task) {
        return null;
    }

    @Override
    public Task createTask(Task task, TaskStatus status) {
        return null;
    }

    @Override
    public void updateTaskStatus(Task task, TaskStatus status) {

    }
}