package me.leozdgao.beaver.infrastructure;

import me.leozdgao.beaver.spi.TaskPersistenceService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

public class TaskPersistenceServiceImpl implements TaskPersistenceService {
    @Override
    public void createTask(Task task) {
        createTask(task, TaskStatus.REQUESTING);
    }

    @Override
    public void createTask(Task task, TaskStatus status) {
        System.out.printf("Create task: %s, status %s\n", task, status);
    }

    @Override
    public void updateTaskStatus(Task task, TaskStatus status) {
        System.out.printf("Update task: %s, status %s\n", task, status);
    }
}
