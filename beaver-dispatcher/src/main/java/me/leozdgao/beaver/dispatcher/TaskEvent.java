package me.leozdgao.beaver.dispatcher;

import lombok.Data;

/**
 * @author leozdgao
 */
@Data
public class TaskEvent {
    private long seq;
    private Task task;
}
