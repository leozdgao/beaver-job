package me.leozdgao.beaver.runtime;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Beaver 任务执行器实现，基于 Disruptor 的高性能阻塞队列，支持：
 * - 定时任务
 * - 超时重试（指数退避）
 * - 指定任务单机并发数控制
 * @author leozdgao
 */
public class BeaverTaskThreadPoolService extends AbstractExecutorService {
    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(Runnable command) {
        Executors.newScheduledThreadPool(8);
    }
}
