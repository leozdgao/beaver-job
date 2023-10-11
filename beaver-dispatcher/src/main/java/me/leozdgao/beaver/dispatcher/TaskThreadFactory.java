package me.leozdgao.beaver.dispatcher;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leozdgao
 */
public class TaskThreadFactory implements ThreadFactory {
    public static final TaskThreadFactory INSTANCE = new TaskThreadFactory();

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName("task-handler-" + counter.incrementAndGet());
        t.setDaemon(false);

        return t;
    }
}
