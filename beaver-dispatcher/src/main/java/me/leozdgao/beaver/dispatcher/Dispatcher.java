package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * @author leozdgao
 */
public class Dispatcher {
    private Disruptor<TaskEvent> disruptor;

    public void init() {
        // 基于 ZK 选主
        // 主节点启动轮训节点 check REQ 状态的任务

        // 初始化等待队列
        TaskEventFactory taskEventFactory = new TaskEventFactory();
        disruptor = new Disruptor<>(taskEventFactory, 1024, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new TaskEventHandler());
        disruptor.start();
    }

    public boolean accept(Task task) {
        // 首先落库, 状态为 REQ

        // 成功则修改任务状态为 WAITING
        // 入等待队列
        // 失败则改状态为 REQ
        disruptor.publishEvent((e, l) -> {
            e.setTask(task);
            e.setSeq(l);
        });

        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init();

        for (int i = 0, l = 1000; i < l; i++) {
            dispatcher.accept(new Task());
        }


        Thread.sleep(5 * 1000);
    }
}
