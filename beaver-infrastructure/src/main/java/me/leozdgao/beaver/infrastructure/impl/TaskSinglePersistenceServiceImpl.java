package me.leozdgao.beaver.infrastructure.impl;

import jakarta.inject.Inject;
import me.leozdgao.beaver.infrastructure.converter.TaskConverter;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于单库单表的任务持久化实现
 * @author zhendong.gzd
 */
public class TaskSinglePersistenceServiceImpl implements TaskPersistenceCommandService {
    private final TaskMapper taskMapper;

    private final TaskConverter taskConverter;

    @Inject
    public TaskSinglePersistenceServiceImpl(TaskMapper taskMapper, TaskConverter taskConverter) {
        this.taskMapper = taskMapper;
        this.taskConverter = taskConverter;
    }

    @Override
    public Task createTask(Task task) {
        return createTask(task, TaskStatus.REQUESTING);
    }

    @Override
    public Task createTask(Task task, TaskStatus status) {
        // TODO: 雪花算法生成 ID
        System.out.printf("Create task: %s, status %s\n", task, status);

        task.setStatus(status);
        List<TaskDO> taskDOList = new ArrayList<>();
        taskDOList.add(taskConverter.convert(task));
        taskMapper.batchCreateTask(taskDOList);

        TaskDO taskDO = taskDOList.get(0);
        return taskConverter.convert(taskDO);
    }

    @Override
    public void updateTaskStatus(Task task, TaskStatus status) {
        if (task == null || task.getId() == null) {
            throw new InvalidParameterException("找不到要更新的任务");
        }

        taskMapper.updateTaskStatus(task.getId(), status.getCode());
    }
}
