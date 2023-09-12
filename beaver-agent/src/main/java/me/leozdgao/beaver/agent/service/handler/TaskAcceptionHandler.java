package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leozdgao.beaver.worker.protocol.ExecTaskCommandPacket;

public class TaskAcceptionHandler extends SimpleChannelInboundHandler<ExecTaskCommandPacket> {
    public static final TaskAcceptionHandler INSTANCE = new TaskAcceptionHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ExecTaskCommandPacket execTaskCommandPacket) throws Exception {
        System.out.println("Task received!!!");
        System.out.println(execTaskCommandPacket);
    }
}
