package me.leozdgao.beaver.runtime;

/**
 * 任务统一接口
 * @param <T> 任务执行结果类型
 * @author leozdgao
 */
public interface BeaverTask<T> {
    /**
     * 执行任务
     * @param ctx 任务上下文
     * @return 任务执行结果
     */
    T execute(BeaverTaskContext ctx);
}
