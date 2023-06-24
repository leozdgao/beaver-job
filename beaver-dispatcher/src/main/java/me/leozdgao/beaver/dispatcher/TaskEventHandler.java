package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.EventHandler;

/**
 * @author leozdgao
 */
public class TaskEventHandler implements EventHandler<TaskEvent> {

    public void onEvent(TaskEvent event, long l, boolean b) throws Exception {
        System.out.println(event);

        try {
            String name = Thread.currentThread().getName();
            System.out.println(String.format("My name %s, %s", name, event.getTask().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(500);

    }
}
