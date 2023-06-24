package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.EventFactory;

/**
 * @author leozdgao
 */
public class TaskEventFactory implements EventFactory<TaskEvent> {

    public TaskEvent newInstance() {
        return new TaskEvent();
    }
}
