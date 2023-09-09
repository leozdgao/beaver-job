package me.leozdgao.beaver.worker;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.leozdgao.beaver.worker.lb.WorkerLoadBalancer;
import me.leozdgao.beaver.worker.sd.ServiceDiscovery;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author leozdgao
 */
@Singleton
public class WorkerManager {
    private final ServiceDiscovery serviceDiscovery;
    private final WorkerLoadBalancer workerLoadBalancer;

    private List<Worker> availableWorkers;

    @Inject
    public WorkerManager(ServiceDiscovery serviceDiscovery, WorkerLoadBalancer workerLoadBalancer) {
        this.serviceDiscovery = serviceDiscovery;
        this.workerLoadBalancer = workerLoadBalancer;
    }

    public Worker getNextWorker(String scope) {
        List<Worker> workers = serviceDiscovery.getCurrentWorkers(scope);
        return workerLoadBalancer.getNextAvailable(workers);
    }

    public void connect(Worker worker, Runnable after, Consumer<Throwable> errorHandler) {
        // 如果没有连接，则创建连接，成功后执行 after 方法
        // 如果连接已经存在，则直接执行 after 方法
    }

    public void sendCommand(Worker worker, Runnable after, Consumer<Throwable> errorHandler) {
        // 需要收到 worker 反馈 ACK 后，才执行 after
    }
}
