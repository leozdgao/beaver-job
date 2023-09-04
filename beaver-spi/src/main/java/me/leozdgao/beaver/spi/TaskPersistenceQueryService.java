package me.leozdgao.beaver.spi;

import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.spi.model.Task;

/**
 * 任务持久化查询接口（任务读接口）
 * @author leozdgao
 */
public interface TaskPersistenceQueryService {
    /**
     * 查询任务列表
     * @param query 查询对象
     * @param needCount 是否需要返回总数
     * @return 任务列表
     */
    PageData<Task> findTasks(TaskListQuery query, boolean needCount);

    /**
     * 根据ID查询任务
     * @param id ID
     * @return 目标任务
     */
    Task findTaskById(Long id);
}
