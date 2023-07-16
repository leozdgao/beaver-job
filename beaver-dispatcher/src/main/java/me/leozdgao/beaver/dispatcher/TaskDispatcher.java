package me.leozdgao.beaver.dispatcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import jakarta.inject.Inject;
import me.leozdgao.beaver.dispatcher.config.DispatcherModule;
import me.leozdgao.beaver.dispatcher.config.DispatcherModule.RingBufferSize;
import me.leozdgao.beaver.spi.TaskPersistenceService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;

/**
 * @author leozdgao
 */
public class TaskDispatcher {
    private Disruptor<TaskEvent> disruptor;

    private TaskPersistenceService taskPersistenceService;

    private EventHandler<TaskEvent> eventHandler;

    private int ringBufferSize = 1024;

    @Inject
    public TaskDispatcher(
        TaskPersistenceService taskPersistenceService,
        EventHandler<TaskEvent> eventHandler,
        @RingBufferSize int ringBufferSize) {

        this.taskPersistenceService = taskPersistenceService;
        this.eventHandler = eventHandler;
        this.ringBufferSize = ringBufferSize;
    }

    /**
     * 初始化
     */
    public void init() {
        // 基于 ZK 选主
        // 主节点启动轮训节点 check REQ 状态的任务

        // 初始化等待队列
        TaskEventFactory taskEventFactory = new TaskEventFactory();
        // FIXME: 线程池
        disruptor = new Disruptor<>(taskEventFactory, ringBufferSize, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
    }

    /**
     * 销毁
     */
    public void dispose() {
        disruptor.shutdown();
    }

    public boolean accept(Task task) {
        // 首先落库, 直接把状态设置为 WAITING
        taskPersistenceService.createTask(task, TaskStatus.REQUESTING);

        // 成功则修改任务状态为 WAITING
        // 入等待队列
        // 失败则改状态为 REQ
        // FIXME: 入队失败是否抛出异常？入队超时时间？
        try {
            disruptor.publishEvent((e, l) -> {
                // NOTE: 这个函数不论执行成功与否，event 都会被 publish
                e.setTask(task);
                e.setSeq(l);

                taskPersistenceService.updateTaskStatus(task, TaskStatus.WAITING);
            });
            return true;
        } catch (Exception e) {
            // 如果最终入队列失败，记录日志
        }

        return false;
    }
}
