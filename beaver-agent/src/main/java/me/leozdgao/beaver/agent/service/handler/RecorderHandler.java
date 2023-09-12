package me.leozdgao.beaver.agent.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于channel的生命周期做一些日志记录或统计
 * @author leozdgao
 */
@Slf4j
public class RecorderHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        log.info("channel {} active!", channelId);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        log.info("channel {} inactive!", channelId);

        super.channelInactive(ctx);
    }
}
