package me.leozdgao.beaver.runtime;

import java.util.function.Consumer;

/**
 * Beaver任务执行器
 * @author leozdgao
 */
public interface BeaverTaskExecutorService {
    /**
     * 执行任务
     * @param consumer 任务执行逻辑
     */
    void execute(Consumer<BeaverTaskContext> consumer);
}
