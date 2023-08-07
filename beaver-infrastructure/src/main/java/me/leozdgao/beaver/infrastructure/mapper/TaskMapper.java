package me.leozdgao.beaver.infrastructure.mapper;

import java.util.List;

import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import org.apache.ibatis.annotations.Select;

public interface TaskMapper {
    TaskDO createTask(TaskDO taskDO);

    @Select("select * from tasks")
    List<TaskDO> findAllTask();
}
