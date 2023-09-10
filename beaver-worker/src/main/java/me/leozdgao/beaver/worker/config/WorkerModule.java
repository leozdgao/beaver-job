package me.leozdgao.beaver.worker.config;

import com.google.inject.AbstractModule;
import me.leozdgao.beaver.spi.BeaverProperties;
import me.leozdgao.beaver.worker.lb.RandomLoadBalancer;
import me.leozdgao.beaver.worker.lb.WorkerLoadBalancer;
import me.leozdgao.beaver.worker.sd.ServiceDiscovery;
import me.leozdgao.beaver.worker.sd.ServiceRegistry;
import me.leozdgao.beaver.worker.sd.ZookeeperServiceDiscovery;
import me.leozdgao.beaver.worker.sd.ZookeeperServiceRegistry;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;


/**
 * Worker管理相关模块配置
 * @author leozdgao
 */
public class WorkerModule extends AbstractModule {
    private static final String ZK_BASE_NODE_PATH = "/beaver_sd";

    private final BeaverProperties beaverProperties;

    public WorkerModule(BeaverProperties properties) {
        beaverProperties = properties;
    }

    @Override
    protected void configure() {
        String type = beaverProperties.getProperty("sd.type");
        if ("zk".equals(type)) {
            CuratorFramework client = curatorFramework();
            bind(ServiceDiscovery.class).toInstance(zookeeperServiceDiscovery(client));
            bind(ServiceRegistry.class).toInstance(zookeeperServiceRegistry(client));
        }

        bind(WorkerLoadBalancer.class).to(RandomLoadBalancer.class);
    }

    private CuratorFramework curatorFramework() {
        String zkConnectionString = beaverProperties.getProperty("sd.zk.connection");
        if (zkConnectionString == null) {
            throw new IllegalArgumentException(
                    "ServiceDiscovery failed: config sd.zk.connection should provide if sd.type is zk");
        }
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zkConnectionString, new ExponentialBackoffRetry(1000, 3));

        try {
            client.start();

            Stat zkRoot = client.checkExists().forPath(ZK_BASE_NODE_PATH);
            if (zkRoot == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(ZK_BASE_NODE_PATH);
            }
        } catch (Exception e) {
            IOUtils.closeQuietly(client);
            throw new RuntimeException(String.format("zk init failed: %s", e));
        }

        return client;
    }

    private org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery(CuratorFramework client) {
        return ServiceDiscoveryBuilder.builder(Object.class)
                        .client(client)
                        .basePath(ZK_BASE_NODE_PATH)
                        .watchInstances(true)
                        .build();
    }

    private ZookeeperServiceDiscovery zookeeperServiceDiscovery(CuratorFramework client) {
        org.apache.curator.x.discovery.ServiceDiscovery<Object> serviceDiscovery = curatorServiceDiscovery(client);
        return new ZookeeperServiceDiscovery(client, serviceDiscovery);
    }

    private ZookeeperServiceRegistry zookeeperServiceRegistry(CuratorFramework client) {
        org.apache.curator.x.discovery.ServiceDiscovery<Object> serviceDiscovery = curatorServiceDiscovery(client);
        return new ZookeeperServiceRegistry(client, serviceDiscovery);
    }
}
