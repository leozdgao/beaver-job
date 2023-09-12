package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.leozdgao.beaver.worker.protocol.Packet;

import java.util.HashMap;
import java.util.Map;

public class CommandStrategyHandler extends SimpleChannelInboundHandler<Packet> {
    public static final CommandStrategyHandler INSTANCE = new CommandStrategyHandler();

    private final Map<Byte, SimpleChannelInboundHandler<? extends Packet>> handlerMap = new HashMap<>();

    public CommandStrategyHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {

    }
}
