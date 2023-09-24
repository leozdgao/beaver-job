package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leozdgao.beaver.agent.service.DefaultChannelProvider;
import me.leozdgao.beaver.runtime.BeaverTaskExecutor;
import me.leozdgao.beaver.worker.protocol.ExecTaskCommandPacket;
import me.leozdgao.beaver.worker.protocol.TaskReceivedPacket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author leozdgao
 */
@ChannelHandler.Sharable
public class TaskAcceptationHandler extends SimpleChannelInboundHandler<ExecTaskCommandPacket> {
    public static final TaskAcceptationHandler INSTANCE = new TaskAcceptationHandler();

    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExecTaskCommandPacket pkt) throws Exception {
        System.out.println("Task received!!!");
        System.out.println(pkt);

        // TODO: 检查当前环境是否支持执行该任务

        TaskReceivedPacket packet = new TaskReceivedPacket(pkt.getTraceId());
        packet.setAccepted(true);
        // 异步写入
        ctx.channel().writeAndFlush(packet);

        // 提交线程池开始执行
        executorService.execute(new BeaverTaskExecutor(pkt, new DefaultChannelProvider(ctx.channel())));
    }
}
