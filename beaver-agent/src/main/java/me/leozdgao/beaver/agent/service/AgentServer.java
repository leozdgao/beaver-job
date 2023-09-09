package me.leozdgao.beaver.agent.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
    private void startBind(int port, Runnable afterSuccess) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture future = bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
                        // ch.pipeline();
                    }
                })
                .bind(port);
        future.addListener(f -> {
            if (f.isSuccess()) {
                log.info("agent server listening...");
                try {
                    afterSuccess.run();
                } catch (Exception e) {
                    ChannelFuture closeFuture = future.channel().close().sync();
                    if (closeFuture.isSuccess()) {
                        log.info("agent server closed");
                    } else {
                        log.info("agent server close failed: {}", closeFuture.cause().toString());
                    }
                }
            } else {
                // if () {
                //     startBind(port + 1, afterSuccess);
                // }

                log.error("agent start failed: {}", f.cause().toString());
                throw new RuntimeException(String.format("Agent Server 启动失败: %s", f.cause().toString()));
            }
        });
    }

    @PostConstruct
    public void init() {
        startBind(10020, () -> {
            // 启动成功后立刻进行服务注册
        });
    }
}
