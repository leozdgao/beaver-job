package me.leozdgao.beaver.agent.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.agent.service.handler.RecorderHandler;
import me.leozdgao.beaver.agent.service.handler.TaskAcceptationHandler;
import me.leozdgao.beaver.agent.utils.IpUtils;
import me.leozdgao.beaver.worker.Worker;
import me.leozdgao.beaver.worker.protocol.PacketCodecHandler;
import me.leozdgao.beaver.worker.protocol.Splitter;
import me.leozdgao.beaver.worker.sd.ServiceRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.BindException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Agent TCP 服务器，用于和 Dispatcher 连接，接受任务以及反馈任务结果
 * - 多个任务的下发复用同一条连接
 * - 任务在执行过程中持续发送心跳保活
 * - 没有任务时，不再发送心跳，让调度器自动断开连接
 * @author leozdgao
 */
@Slf4j
@Component
public class AgentServer {
    private static final int AGENT_TCP_SERVER_PORT = 10020;

    private static final String AGENT_DEFAULT_SCOPE = "DEFAULT";

    @Resource
    private ServiceRegistry serviceRegistry;

    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    private void startServer(int port, Consumer<Integer> afterSuccess) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture future = bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new Splitter());
                        ch.pipeline().addLast(new RecorderHandler());
                        ch.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        ch.pipeline().addLast(TaskAcceptationHandler.INSTANCE);
                    }
                })
                .bind(port);
        future.addListener(f -> {
            if (f.isSuccess()) {
                log.info("agent server listening in {}", port);
                try {
                    afterSuccess.accept(port);
                } catch (Exception e) {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                    ChannelFuture closeFuture = future.channel().close().sync();
                    if (closeFuture.isSuccess()) {
                        log.info("agent server closed");
                    } else {
                        log.info("agent server close failed: {}", closeFuture.cause().toString());
                    }
                }
            } else {
                if (f.cause() instanceof BindException) {
                    startServer(port + 1, afterSuccess);
                } else {
                    log.error("agent start failed: {}", f.cause().toString());
                    throw new RuntimeException(String.format("Agent Server 启动失败: %s", f.cause().toString()));
                }
            }
        });
    }

    @PostConstruct
    public void init() {
        startServer(AGENT_TCP_SERVER_PORT, (port) -> {
            try {
                serviceRegistry.start();
            } catch (Exception e) {
                log.error("agent register start failed: {}", e.toString());
                throw new RuntimeException(String.format("Agent Server 服务注册启动失败: %s", e));
            }

            Collection<InetAddress> addresses;
            try {
                addresses = IpUtils.getAllLocalIPs();
            } catch (Exception e) {
                log.error("agent register failed, get local id failed: {}", e.toString());
                throw new RuntimeException(String.format("Agent Server 服务注册失败: %s", e));
            }

            if (addresses.size() == 0) {
                throw new RuntimeException("Agent Server 服务注册失败: 无法获取本地 IP");
            }

            String ip = addresses.iterator().next().getHostAddress();

            // 启动成功后立刻进行服务注册
            try {
                serviceRegistry.regsiter(new Worker(AGENT_DEFAULT_SCOPE, ip, port));
            } catch (Exception e) {
                throw new RuntimeException(String.format("Agent Server 服务注册失败: %s", e));
            }
        });
    }

    @PreDestroy
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
