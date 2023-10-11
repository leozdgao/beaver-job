package me.leozdgao.beaver.worker.protocol.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class TracingHandler extends ChannelDuplexHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 查看 msg 是否是可被解析的 Packet
        // 从 packet 解析看是否有 tracingId
        // 如果没有则 startTrace
        // 如果有则计算下一个 spanId

        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 查看 msg 是否是可被解析的 Packet
        // 从 packet 解析看是否有 tracingId
        // 如果没有则 startTrace
        // 如果有则计算下一个 spanId
        super.write(ctx, msg, promise);
    }
}
