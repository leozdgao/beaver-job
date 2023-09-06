package me.leozdgao.beaver.infrastructure.converter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leozdgao
 */
@Singleton
public class TaskConverter {
    private final Gson gson;

    @Inject
    public TaskConverter(Gson gson) {
        this.gson = gson;
    }

    public Task convert(TaskDO taskDO) {
        if (taskDO == null) {
            return null;
        }

        return Task.builder()
            .id(taskDO.getId())
            .type(taskDO.getType())
            .scope(taskDO.getScope())
            .status(TaskStatus.of(taskDO.getStatus()))
            .payload(gson.fromJson(taskDO.getPayload(), new TypeToken<Map<String, Object>>() {}.getType()))
            .result(gson.fromJson(taskDO.getResult(), new TypeToken<Map<String, Object>>() {}.getType()))
            .ext(gson.fromJson(taskDO.getExtra(), new TypeToken<Map<String, Object>>() {}.getType()))
            .build();
    }

    public TaskDO convert(Task task) {
        if (task == null) {
            return null;
        }

        TaskDO taskDO = new TaskDO();
        taskDO.setId(task.getId());
        taskDO.setType(task.getType());
        taskDO.setScope(task.getScope());
        taskDO.setStatus(task.getStatus().getCode());
        taskDO.setPayload(gson.toJson(task.getPayload()));
        taskDO.setResult(gson.toJson(task.getResult()));
        taskDO.setExtra(gson.toJson(task.getExt()));

        return taskDO;
    }

    public List<Task> convertTaskList(List<TaskDO> taskDOList) {
        if (taskDOList == null) {
            return null;
        }

        List<Task> taskList = new ArrayList<>();

        for (TaskDO taskDO : taskDOList) {
            taskList.add(convert(taskDO));
        }

        return taskList;
    }
}
