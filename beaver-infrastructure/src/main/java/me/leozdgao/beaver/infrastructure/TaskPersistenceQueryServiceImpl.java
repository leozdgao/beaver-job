package me.leozdgao.beaver.infrastructure;

import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import me.leozdgao.beaver.spi.model.Task;

import java.util.List;

/**
 * @author leozdgao
 */
public class TaskPersistenceQueryServiceImpl implements TaskPersistenceQueryService {
    private final TaskMapper taskMapper;

    public TaskPersistenceQueryServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public PageData<Task> findTasks(TaskListQuery query, boolean needCount) {
        List<TaskDO> taskDOList = taskMapper.findTaskPage(query);

        return null;
    }

    @Override
    public Task findTaskById(Long id) {
        TaskDO taskDO = taskMapper.findOneById(id);
        return null;
    }
}
