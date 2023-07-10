package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * @author leozdgao
 */
public class TaskDispatcher {
    /**
     * 用于阻塞队列的 RingBuffer 最大长度
     */
    private static final int RING_BUFFER_SIZE = 1024;

    private Disruptor<TaskEvent> disruptor;

    private TaskPersistenceService taskPersistenceService;

    public TaskDispatcher(TaskPersistenceService taskPersistenceService) {
        this.taskPersistenceService = taskPersistenceService;
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
        disruptor = new Disruptor<>(taskEventFactory, RING_BUFFER_SIZE, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new TaskEventHandler());
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
        taskPersistenceService.createTask(task, TaskStatus.WAITING);

        // 成功则修改任务状态为 WAITING
        // 入等待队列
        // 失败则改状态为 REQ
        // FIXME: 入队失败是否抛出异常？入队超时时间？
        try {
            disruptor.publishEvent((e, l) -> {
                e.setTask(task);
                e.setSeq(l);
            });
            return true;
        } catch (Exception e) {
            // 如果最终入队列失败，
            taskPersistenceService.updateTaskStatus(TaskStatus.REQUESTING);
        }

        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        //TaskDispatcher dispatcher = new TaskDispatcher();
        //dispatcher.init();
        //
        //for (int i = 0, l = 1000; i < l; i++) {
        //    dispatcher.accept(new Task());
        //}
        //
        //
        //Thread.sleep(5 * 1000);
    }
}
