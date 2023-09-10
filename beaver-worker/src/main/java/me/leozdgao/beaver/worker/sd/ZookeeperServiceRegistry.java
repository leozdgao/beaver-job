package me.leozdgao.beaver.worker.sd;

import me.leozdgao.beaver.worker.Worker;
import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * @author leozdgao
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {
    private final org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery;
    private final CuratorFramework client;

    public ZookeeperServiceRegistry(
            CuratorFramework client,
            org.apache.curator.x.discovery.ServiceDiscovery<Object> curatorServiceDiscovery) {
        this.client = client;
        this.curatorServiceDiscovery = curatorServiceDiscovery;
    }

    @Override
    public void start() throws Exception {
        curatorServiceDiscovery.start();
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(curatorServiceDiscovery);
        IOUtils.closeQuietly(client);
    }

    @Override
    public void regsiter(Worker worker) throws Exception {
        worker.setRegisterTime(System.currentTimeMillis());

        ServiceInstance<Object> serviceInstance =
                ZookeeperWorkerConvertor.convert(worker);
        curatorServiceDiscovery.registerService(serviceInstance);
    }
}
