package me.leozdgao.beaver.infrastructure.impl;

import com.alibaba.cola.statemachine.StateMachine;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.infrastructure.converter.TaskConverter;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskTransitionEvent;
import me.leozdgao.beaver.spi.model.TaskStatus;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于单库单表的任务持久化实现
 * @author zhendong.gzd
 */
@Slf4j
public class TaskSinglePersistenceServiceImpl implements TaskPersistenceCommandService {
    private final SqlSessionFactory sqlSessionFactory;

    private final TaskMapper taskMapper;

    private final TaskConverter taskConverter;

    private final Gson gson;

    private final StateMachine<TaskStatus, TaskTransitionEvent, Object> stateMachine;

    @Inject
    public TaskSinglePersistenceServiceImpl(
            TaskMapper taskMapper, TaskConverter taskConverter, SqlSessionFactory sqlSessionFactory,
            Gson gson, StateMachine<TaskStatus, TaskTransitionEvent, Object> stateMachine) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.taskMapper = taskMapper;
        this.taskConverter = taskConverter;
        this.gson = gson;
        this.stateMachine = stateMachine;
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
    public void taskSuccess(Task task, Map<String, Object> payload) {
        String result = gson.toJson(payload);
        updateTaskStatus(task, TaskTransitionEvent.SUCCESS, result);
    }

    @Override
    public void taskFailed(Task task, Throwable cause) {
        Map<String, Object> payload = new HashMap<>(4);
        payload.put("msg", cause.toString());

        String result = gson.toJson(payload);
        updateTaskStatus(task, TaskTransitionEvent.FAIL, result);
    }

    @Override
    public void updateTaskStatus(Task task, TaskTransitionEvent event) {
        updateTaskStatus(task, event, null);
    }

    private void updateTaskStatus(Task task, TaskTransitionEvent event, String result) {
        // 这里要开启事务，先查询，再根据查询的状态校验状态转移的合法性，再更新为失败态
        if (task == null || task.getId() == null) {
            throw new InvalidParameterException("找不到要更新的任务");
        }

        if (task.getStatus() == null) {
            throw new InvalidParameterException("任务状态异常为 null");
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            try {
                TaskMapper mapperInTrx = sqlSession.getMapper(TaskMapper.class);
                TaskDO currentTask = mapperInTrx.findOneById(task.getId());
                TaskStatus fromStatus = TaskStatus.of(currentTask.getStatus());
                TaskStatus toStatus = stateMachine.fireEvent(fromStatus, event, null);

                if (fromStatus.equals(toStatus)) {
                    // 无需更新任务状态
                    log.warn("任务 {} 状态 {} 事件 {}，未满足状态机定义，状态未更新", task.getId(), fromStatus, event);
                } else {
                    mapperInTrx.updateTaskStatus(task.getId(), fromStatus.getCode(), toStatus.getCode(), result);
                }

                sqlSession.commit();
            } catch (Exception e) {
                log.error("更新任务状态失败：{}", e.toString());
                sqlSession.rollback();
            }
        }
    }
}
