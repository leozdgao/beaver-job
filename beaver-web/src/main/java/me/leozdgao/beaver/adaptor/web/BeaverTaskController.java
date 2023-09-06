package me.leozdgao.beaver.adaptor.web;

import me.leozdgao.beaver.client.Response;
import me.leozdgao.beaver.client.dto.PageData;
import me.leozdgao.beaver.client.dto.TaskCreationCommand;
import me.leozdgao.beaver.client.dto.TaskCreationDTO;
import me.leozdgao.beaver.client.dto.TaskListQuery;
import me.leozdgao.beaver.service.TaskService;
import me.leozdgao.beaver.spi.model.Task;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 任务中心 Web 层接口
 * @author leozdgao
 */
@RequestMapping("api/v1/tasks")
@RestController
public class BeaverTaskController {
    @Resource
    private TaskService taskService;


    @GetMapping("findTasks")
    public Response<PageData<Task>> findTasks(TaskListQuery query) {
        PageData<Task> tasks = taskService.findTasks(query);
        return Response.buildSuccess(tasks);
    }

    @PostMapping("createTask")
    public Response<TaskCreationDTO> createTask(@RequestBody TaskCreationCommand cmd) {
        Assert.hasText(cmd.getTaskType(), "任务类型 taskType 必填");

        Task task = Task.builder()
                .type(cmd.getTaskType())
                .payload(cmd.getPayload())
                .ext(cmd.getExtra())
                .build();

        Task createdTask = taskService.createTask(task);

        return Response.buildSuccess(
                TaskCreationDTO.builder()
                        .taskId(createdTask.getId())
                        .build()
        );
    }
}
