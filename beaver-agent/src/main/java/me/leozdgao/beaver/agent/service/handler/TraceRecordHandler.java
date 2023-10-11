package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import me.leozdgao.beaver.worker.protocol.Packet;
import me.leozdgao.beaver.worker.utils.TraceUtils;

/**
 * @author leozdgao
 */
@ChannelHandler.Sharable
public class TraceRecordHandler extends ChannelInboundHandlerAdapter {
    public final static TraceRecordHandler INSTANCE = new TraceRecordHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet) {
            Packet pkt = (Packet) msg;
            String traceId = pkt.getTraceId();
            if (traceId != null && !traceId.isEmpty()) {
                TraceUtils.setTraceId(traceId);
            }
        }

        ctx.fireChannelRead(msg);
    }
}
