package me.leozdgao.beaver.worker.config;

import com.google.inject.AbstractModule;
import me.leozdgao.beaver.spi.BeaverProperties;
import me.leozdgao.beaver.worker.lb.RandomLoadBalancer;
import me.leozdgao.beaver.worker.lb.WorkerLoadBalancer;
import me.leozdgao.beaver.worker.sd.ServiceDiscovery;
import me.leozdgao.beaver.worker.sd.ZookeeperServiceDiscovery;
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
            bind(ServiceDiscovery.class).toInstance(zookeeperServiceDiscovery());
        }

        bind(WorkerLoadBalancer.class).to(RandomLoadBalancer.class);
    }

    private ZookeeperServiceDiscovery zookeeperServiceDiscovery() {
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
            throw new RuntimeException(String.format("zk init failed: %s", e));
        }

        org.apache.curator.x.discovery.ServiceDiscovery<Object> serviceDiscovery =
                ServiceDiscoveryBuilder.builder(Object.class)
                        .client(client)
                        .basePath(ZK_BASE_NODE_PATH)
                        .watchInstances(true)
                        .build();

        ZookeeperServiceDiscovery zookeeperServiceDiscovery = new ZookeeperServiceDiscovery(serviceDiscovery);
        try {
            zookeeperServiceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(String.format("zk serviceDiscovery start failed: %s", e));
        }

        return zookeeperServiceDiscovery;
    }
}
