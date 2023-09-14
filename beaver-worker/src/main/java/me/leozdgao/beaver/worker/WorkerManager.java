package me.leozdgao.beaver.worker;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.worker.lb.WorkerLoadBalancer;
import me.leozdgao.beaver.worker.protocol.*;
import me.leozdgao.beaver.worker.sd.ServiceDiscovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author leozdgao
 */
@Slf4j
@Singleton
public class WorkerManager {
    private final ServiceDiscovery serviceDiscovery;
    private final WorkerLoadBalancer workerLoadBalancer;
    private final TrustedSender trustedSender;
    private final Map<String, Channel> workerConnections = new ConcurrentHashMap<>();
    private Bootstrap clientBootstrap;
    private NioEventLoopGroup workerGroup;
    private final Object startLock = new Object();
    private final Map<String, Lock> workerConnectionLocks = new ConcurrentHashMap<>();

    @Inject
    public WorkerManager(ServiceDiscovery serviceDiscovery, WorkerLoadBalancer workerLoadBalancer, TrustedSender trustedSender) {
        this.serviceDiscovery = serviceDiscovery;
        this.workerLoadBalancer = workerLoadBalancer;
        this.trustedSender = trustedSender;
    }

    public void start() {
        if (workerGroup == null && clientBootstrap == null) {
            synchronized (startLock) {
                if (workerGroup == null && clientBootstrap == null) {
                    workerGroup = new NioEventLoopGroup();
                    clientBootstrap = new Bootstrap();
                    clientBootstrap.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<>() {
                                @Override
                                protected void initChannel(Channel ch) {
                                    ch.pipeline().addLast(new Splitter());
                                    ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                                    ch.pipeline().addLast(trustedSender.pipelineHandler());
                                }
                            });
                }
            }
        }
    }

    public void close() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }

    public Worker getNextWorker(String scope) {
        List<Worker> workers = serviceDiscovery.getCurrentWorkers(scope);
        return workerLoadBalancer.getNextAvailable(workers);
    }

    public void connect(Worker worker, Runnable after, Consumer<Throwable> errorHandler) {
        // 如果没有连接，则创建连接，成功后执行 after 方法
        // 如果连接已经存在，则直接执行 after 方法
        String workerId = worker.getId();
        Channel channel = workerConnections.get(workerId);

        if (channel != null && channel.isActive()) {
            log.info("connection to {} existed", workerId);
            after.run();
            return;
        }

        // 加把锁，一个 worker 同时只能有一个线程尝试建立连接
        Lock lock = workerConnectionLocks.compute(workerId, (k, l) -> l == null ? new ReentrantLock() : l);
        lock.lock();

        try {
            // 对 channel 二次判断
            Channel channelAgain = workerConnections.get(workerId);

            if (channelAgain != null && channelAgain.isActive()) {
                log.info("connection to {} existed", workerId);
                after.run();
                return;
            }

            ChannelFuture future = clientBootstrap.connect(worker.getAddress()).sync();
            if (future.isSuccess()) {
                Channel newChannel = future.channel();
                workerConnections.put(workerId, newChannel);

                log.info("connection to {} created", workerId);

                after.run();
            } else {
                errorHandler.accept(future.cause());
            }
        } catch (Exception e) {
            errorHandler.accept(e);
        } finally {
            lock.unlock();
        }
    }

    public void sendTask(Worker worker, Task task, Runnable after, Consumer<Throwable> errorHandler) {
        // 需要收到 worker 反馈 ACK 后，才执行 after
        String workerId = worker.getId();
        Channel channel = workerConnections.get(workerId);

        if (channel == null) {
            errorHandler.accept(new IllegalArgumentException(String.format("找不到 worker %s", workerId)));
            return;
        }

        ExecTaskCommandPacket packet = ExecTaskCommandPacket.builder()
                .taskId(task.getId())
                .taskType(task.getType())
                .payload(task.getPayload())
                .extra(task.getExt())
                .build();
        Promise<Boolean> acceptedPromise = trustedSender.sendUntilResponse(channel, packet, 5000);
        acceptedPromise.addListener(f -> {
            if (f.isSuccess()) {
                after.run();
            } else {
                errorHandler.accept(f.cause());
            }
        });

//        ChannelFuture future;
//        try {
//            future = channel.writeAndFlush(packet).sync();
//            // 这里用同步其实会导致性能不太好
//        } catch (InterruptedException e) {
//            errorHandler.accept(e);
//            return;
//        }
//
//        if (future.isSuccess()) {
//            log.info("task send before run after");
//            after.run();
//        } else {
//            errorHandler.accept(future.cause());
//        }
    }
}
