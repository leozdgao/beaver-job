package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leozdgao.beaver.worker.protocol.ExecTaskCommandPacket;
import me.leozdgao.beaver.worker.protocol.TaskReceivedPacket;

@ChannelHandler.Sharable
public class TaskAcceptionHandler extends SimpleChannelInboundHandler<ExecTaskCommandPacket> {
    public static final TaskAcceptionHandler INSTANCE = new TaskAcceptionHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExecTaskCommandPacket execTaskCommandPacket) throws Exception {
        System.out.println("Task received!!!");
        System.out.println(execTaskCommandPacket);

        // TODO: 检查当前环境是否支持执行该任务

        TaskReceivedPacket packet = new TaskReceivedPacket(execTaskCommandPacket.getTraceId());
        packet.setAccepted(true);
        ctx.channel().writeAndFlush(packet);
    }
}
