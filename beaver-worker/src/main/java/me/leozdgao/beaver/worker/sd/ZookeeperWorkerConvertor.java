package me.leozdgao.beaver.worker.sd;

import com.google.inject.Singleton;
import me.leozdgao.beaver.worker.Worker;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;

import java.net.InetSocketAddress;

/**
 * @author leozdgao
 */
@Singleton
public class ZookeeperWorkerConvertor {
    public static ServiceInstance<Object> convert(Worker worker) {
        InetSocketAddress address = worker.getAddress();
        return new ServiceInstance<>(worker.getScope(), worker.getId(), address.getAddress().getHostAddress(), address.getPort(),
                null, null, worker.getRegisterTime(), ServiceType.DYNAMIC, null);
    }

    public static Worker convert(ServiceInstance<Object> serviceInstance) {
        return new Worker(serviceInstance.getName(), serviceInstance.getAddress(), serviceInstance.getPort(), serviceInstance.getId());
    }
}
