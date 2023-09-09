package me.leozdgao.beaver.worker.lb;

import me.leozdgao.beaver.worker.Worker;

import java.util.List;


/**
 * Worker负载均衡器
 * @author leozdgao
 */
public interface WorkerLoadBalancer {
    /**
     * 获取下一个可用Worker实例
     * @param workers worker实例列表
     * @return worker
     */
    Worker getNextAvailable(List<Worker> workers);
}
