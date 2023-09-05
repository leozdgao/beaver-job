package me.leozdgao.beaver.adaptor.web;

import me.leozdgao.beaver.client.Response;
import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskCreationCommand;
import me.leozdgao.beaver.client.dto.TaskCreationDTO;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import me.leozdgao.beaver.spi.model.Task;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 任务中心 Web 层接口
 * @author leozdgao
 */
@RequestMapping("api/v1/tasks")
@RestController
public class BeaverTaskController {
    @Resource
    private TaskPersistenceQueryService taskPersistenceQueryService;

    @Resource
    private TaskPersistenceCommandService taskPersistenceCommandService;

    @GetMapping("findTasks")
    public Response<PageData<Task>> findTasks(TaskListQuery query) {
        PageData<Task> tasks = taskPersistenceQueryService.findTasks(query, true);
        return Response.buildSuccess(tasks);
    }

    @PostMapping("createTask")
    public Response<TaskCreationDTO> createTask(TaskCreationCommand cmd) {
        Assert.hasText(cmd.getTaskType(), "任务类型 taskType 必填");

        Task task = Task.builder()
                .type(cmd.getTaskType())
                .payload(cmd.getPayload())
                .ext(cmd.getExtra())
                .build();

        Task createdTask = taskPersistenceCommandService.createTask(task);

        return Response.buildSuccess(
                TaskCreationDTO.builder()
                        .taskId(createdTask.getId())
                        .build()
        );
    }
}
