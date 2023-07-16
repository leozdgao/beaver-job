package me.leozdgao.beaver.dispatcher;

import lombok.Data;
import me.leozdgao.beaver.spi.model.Task;

/**
 * @author leozdgao
 */
@Data
public class TaskEvent {
    private long seq;
    private Task task;
}
