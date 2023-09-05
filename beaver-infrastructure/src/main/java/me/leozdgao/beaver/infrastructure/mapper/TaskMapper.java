package me.leozdgao.beaver.infrastructure.mapper;

import java.util.List;

import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import org.apache.ibatis.annotations.Select;

/**
 * @author leozdgao
 */
public interface TaskMapper {
    /**
     * 创建任务
     * @param taskDO 任务DO
     */
    void batchCreateTask(List<TaskDO> taskDO);

    /**
     * 任务查询分页接口
     * @param query 查询条件
     * @return 任务列表
     */
    List<TaskDO> findTaskPage(TaskListQuery query);

    /**
     * 根据ID查询任务
     * @param id 任务ID
     * @return 目标任务
     */
    TaskDO findOneById(Long id);

    /**
     * 获取任务数量
     * @param query 查询条件
     * @return 数量
     */
    Long getTaskCount(TaskListQuery query);

    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 任务状态
     */
    void updateTaskStatus(Long id, Integer status);
}
