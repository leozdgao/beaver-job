package me.leozdgao.beaver.worker.sd;

import me.leozdgao.beaver.worker.Worker;

import java.util.List;
import java.util.function.Consumer;

/**
 * 用于 Worker 的服务发现
 * @author leozdgao
 */
public interface ServiceDiscovery {
    void start() throws Exception;
    void close();
    /**
     * 根据域查询当前的 worker 列表
     * @param scope 域
     * @return worker 列表
     */
    List<Worker> getCurrentWorkers(String scope);
}
