package me.leozdgao.beaver.worker.sd;

import me.leozdgao.beaver.worker.Worker;

/**
 * @author leozdgao
 */
public interface ServiceRegistry {
    void start() throws Exception;

    /**
     * 关闭
     */
    void close();
    /**
     * 对服务进行注册
     * @param worker worker实例
     * @exception Exception 注册失败异常
     */
    void regsiter(Worker worker) throws Exception;
}
