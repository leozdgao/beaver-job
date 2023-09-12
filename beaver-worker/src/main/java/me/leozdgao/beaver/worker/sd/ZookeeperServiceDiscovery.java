package me.leozdgao.beaver.worker.sd;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.worker.Worker;
import me.leozdgao.beaver.worker.config.WorkerModule;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.*;
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
    private final CuratorFramework client;
    private Map<String, ServiceCache<Object>> serviceCaches = new HashMap<>();
    private Map<String, Map<String, Worker>> workers = new HashMap<>();

    public ZookeeperServiceDiscovery(
            CuratorFramework client,
            org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery) {
        this.curatorServiceDiscovery = curatorServiceDiscovery;
        this.client = client;
    }

    public void start() throws Exception {
        curatorServiceDiscovery.start();

        ensureScopeNode("DEFAULT");

        Collection<String> scopes = curatorServiceDiscovery.queryForNames();
        scopes.forEach(scope -> {
            ServiceCache<Object> serviceCache = curatorServiceDiscovery.serviceCacheBuilder()
                    .name(scope)
                    .build();
            serviceCache.addListener(new ScopeServiceCacheListener(scope));
            try {
                serviceCache.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            serviceCaches.put(scope, serviceCache);

            try {
                workers.put(scope, syncWorkers(scope, serviceCache.getInstances()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void close() {
        IOUtils.closeQuietly(curatorServiceDiscovery);
        serviceCaches.values().forEach(IOUtils::closeQuietly);
        IOUtils.closeQuietly(client);
    }

    private void ensureScopeNode(String scope) throws Exception {
        String scopePath = String.format("%s/%s", WorkerModule.ZK_BASE_NODE_PATH, scope);
        Stat stat = client.checkExists().forPath(scopePath);

        if (stat == null) {
            client.create().withMode(CreateMode.PERSISTENT).forPath(scopePath);
        }
    }

    private Map<String, Worker> syncWorkers(String scope) throws Exception {
        Collection<ServiceInstance<Object>> serviceInstances = this.curatorServiceDiscovery.queryForInstances(scope);
        return syncWorkers(scope, serviceInstances);
    }

    private Map<String, Worker> syncWorkers(String scope, Collection<ServiceInstance<Object>> serviceInstances) {
        return serviceInstances.stream()
                .map(this::serviceInstanceToWorker)
                .collect(Collectors.toMap(Worker::getId, Function.identity()));
    }

    private Worker serviceInstanceToWorker(ServiceInstance<Object> serviceInstance) {
        return ZookeeperWorkerConvertor.convert(serviceInstance);
    }

    @Override
    public List<Worker> getCurrentWorkers(String scope) {
        Map<String, Worker> scopeWorkers = workers.get(scope);

        if (scopeWorkers != null) {
            return scopeWorkers.values().stream().filter(Worker::isEnabled).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public class ScopeServiceCacheListener implements ServiceCacheListener {
        private final String scope;

        public ScopeServiceCacheListener(String scope) {
            this.scope = scope;
        }

        @Override
        public void cacheChanged() {
            ServiceCache<Object> serviceCache = serviceCaches.get(scope);
            List<ServiceInstance<Object>> serviceInstances = serviceCache.getInstances();
            workers.put(scope, syncWorkers(scope, serviceInstances));
        }

        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {

        }
    }
}
