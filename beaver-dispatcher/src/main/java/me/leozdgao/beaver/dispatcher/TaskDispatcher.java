package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.config.DispatcherModule.RingBufferSize;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;
import me.leozdgao.beaver.spi.model.TaskTransitionEvent;

/**
 * @author leozdgao
 */
@Slf4j
@Singleton
public class TaskDispatcher {
    private Disruptor<TaskEvent> disruptor;

    private final TaskPersistenceCommandService taskPersistenceService;

    private final TaskEventLauncher eventLauncher;

    private int ringBufferSize = 1024;

    private boolean inited = false;

    private final Object initLock = new Object();

    @Inject
    public TaskDispatcher(
        TaskPersistenceCommandService taskPersistenceService,
        TaskEventLauncher eventLauncher,
        @RingBufferSize int ringBufferSize) {

        this.taskPersistenceService = taskPersistenceService;
        this.eventLauncher = eventLauncher;
        this.ringBufferSize = ringBufferSize;
    }

    /**
     * 初始化
     */
    public void init() {
        if (inited) {
            return;
        }

        synchronized (initLock) {
            if (inited) {
                return;
            }

            // 基于 ZK 选主
            // 主节点启动轮训节点 check REQ 状态的任务

            // 初始化等待队列
            TaskEventFactory taskEventFactory = new TaskEventFactory();
            // FIXME: 线程池
            disruptor = new Disruptor<>(taskEventFactory, ringBufferSize, DaemonThreadFactory.INSTANCE);
            disruptor.handleEventsWith(eventLauncher);
            disruptor.start();

            inited = true;
        }
    }

    /**
     * 销毁
     */
    public void close() {
        eventLauncher.close();
        disruptor.shutdown();
    }

    public Task accept(Task task) {
        Task generatedTask = taskPersistenceService.createTask(task, TaskStatus.REQUESTING);

        // 成功则修改任务状态为 WAITING
        // 入等待队列
        // 失败则改状态为 REQ
        // FIXME: 入队失败是否抛出异常？入队超时时间？
        try {
            disruptor.publishEvent((e, l) -> {
                // NOTE: 这个函数不论执行成功与否，event 都会被 publish
                e.setTask(generatedTask);
                e.setSeq(l);

                taskPersistenceService.updateTaskStatus(generatedTask, TaskTransitionEvent.PLAN);
            });
            return generatedTask;
        } catch (Exception e) {
            // 如果最终入队列失败，记录日志
            log.error("任务 {} 入队列失败：{}", generatedTask.getId(), e.toString());
        }

        return null;
    }
}
