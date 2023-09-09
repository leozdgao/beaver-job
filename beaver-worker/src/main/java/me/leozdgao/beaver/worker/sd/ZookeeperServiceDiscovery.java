package me.leozdgao.beaver.worker.sd;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.worker.Worker;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 基于 ZK 的服务发现实现
 * @author leozdgao
 */
@Slf4j
@Singleton
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private final org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery;
    private Map<String, Map<String, Worker>> workers = new HashMap<>();

    public ZookeeperServiceDiscovery(
            org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery) {
        this.curatorServiceDiscovery = curatorServiceDiscovery;
    }

    public void start() throws Exception {
        curatorServiceDiscovery.start();
        Collection<String> scopes = curatorServiceDiscovery.queryForNames();
        scopes.forEach(scope -> {
            try {
                workers.put(scope, syncWorkers(scope));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void close() throws IOException {
        this.curatorServiceDiscovery.close();
    }

    private Map<String, Worker> syncWorkers(String scope) throws Exception {
        Collection<ServiceInstance<Object>> serviceInstances =
                this.curatorServiceDiscovery.queryForInstances(scope);
        return serviceInstances.stream()
                .map(this::serviceInstanceToWorker)
                .collect(Collectors.toMap(Worker::getId, Function.identity()));

    }

    private Worker serviceInstanceToWorker(ServiceInstance<Object> serviceInstance) {
        return Worker.builder()
                .id(serviceInstance.getId())
                .host(serviceInstance.getAddress())
                .port(serviceInstance.getPort())
                .build();
    }

    @Override
    public List<Worker> getCurrentWorkers(String scope) {
        Map<String, Worker> scopeWorkers = workers.get(scope);

        if (scopeWorkers != null) {
            return scopeWorkers.values().stream().filter(Worker::isEnabled).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void subscribe(String scope, Consumer<List<Worker>> consumer) {

    }
}
