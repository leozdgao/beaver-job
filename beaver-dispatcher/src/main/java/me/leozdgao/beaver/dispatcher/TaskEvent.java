package me.leozdgao.beaver.dispatcher;

import lombok.Data;
import me.leozdgao.beaver.spi.model.Task;

import java.util.Map;

/**
 * @author leozdgao
 */
@Data
public class TaskEvent {
    private long seq;
    private Task task;
    private String traceId;
}
