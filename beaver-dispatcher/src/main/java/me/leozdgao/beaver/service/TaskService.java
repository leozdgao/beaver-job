package me.leozdgao.beaver.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.dispatcher.TaskDispatcher;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import me.leozdgao.beaver.spi.model.Task;

/**
 * @author leozdgao
 */
@Singleton
public class TaskService {
    private final TaskPersistenceQueryService taskPersistenceQueryService;
    private final TaskPersistenceCommandService taskPersistenceCommandService;
    private final TaskDispatcher taskDispatcher;

    @Inject
    public TaskService(
            TaskPersistenceQueryService taskPersistenceQueryService,
            TaskPersistenceCommandService taskPersistenceCommandService,
            TaskDispatcher taskDispatcher) {
        this.taskPersistenceCommandService = taskPersistenceCommandService;
        this.taskPersistenceQueryService = taskPersistenceQueryService;
        this.taskDispatcher = taskDispatcher;

        taskDispatcher.init();
    }

    public PageData<Task> findTasks(TaskListQuery query) {
        if (query == null) {
            return null;
        }

        if (query.getScope() == null) {
            query.setScope("DEFAULT");
        }

        return taskPersistenceQueryService.findTasks(query, true);
    }

    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }

        if (task.getScope() == null) {
            task.setScope("DEFAULT");
        }

        Task generatedTask = taskDispatcher.accept(task);

        if (generatedTask == null) {
            throw new RuntimeException("任务队列已满，任务接受失败");
        }

        return generatedTask;
    }
}
