package me.leozdgao.beaver.infrastructure.impl;

import jakarta.inject.Inject;
import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.infrastructure.converter.TaskConverter;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import me.leozdgao.beaver.spi.model.Task;

import java.util.List;
import java.util.Map;

/**
 * @author leozdgao
 */
public class TaskPersistenceQueryServiceImpl implements TaskPersistenceQueryService {
    private final TaskMapper taskMapper;

    private final TaskConverter taskConverter;

    @Inject
    public TaskPersistenceQueryServiceImpl(TaskMapper taskMapper, TaskConverter taskConverter) {
        this.taskMapper = taskMapper;
        this.taskConverter = taskConverter;
    }

    @Override
    public PageData<Task> findTasks(TaskListQuery query, boolean needCount) {
        List<TaskDO> taskDOList = taskMapper.findTaskPage(query);
        List<Task> tasks = taskConverter.convertTaskList(taskDOList);
        long count = -1L;

        if (needCount) {
            count = taskMapper.getTaskCount(query);
        }

        return PageData.<Task>builder()
                .list(tasks)
                .pageIndex(query.getPageIndex())
                .pageSize(query.getPageSize())
                .count(count)
                .build();
    }

    @Override
    public Task findTaskById(Long id) {
        TaskDO taskDO = taskMapper.findOneById(id);
        return taskConverter.convert(taskDO);
    }
}
