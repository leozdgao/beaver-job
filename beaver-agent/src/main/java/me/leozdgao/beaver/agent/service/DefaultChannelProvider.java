package me.leozdgao.beaver.agent.service;

import io.netty.channel.Channel;

/**
 * 为 Agent 提供连接 Dispatcher 的 Channel
 * @author leozdgao
 */
public class DefaultChannelProvider implements ChannelProvider {
    /**
     * 默认频道，当前频道可用的情况下，优先使用当前频道
     */
    private Channel defaultChannel;

    public DefaultChannelProvider(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    private Channel tryConnectToDispatcher() {
        // TODO: 连接到 Dispatcher
        return null;
    }

    @Override
    public Channel getChannel() {
        if (defaultChannel != null && defaultChannel.isOpen()) {
            return defaultChannel;
        }

        return tryConnectToDispatcher();
    }
}
