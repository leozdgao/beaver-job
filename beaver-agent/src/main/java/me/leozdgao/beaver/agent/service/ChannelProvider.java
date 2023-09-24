package me.leozdgao.beaver.agent.service;

import io.netty.channel.Channel;

/**
 * @author leozdgao
 */
public interface ChannelProvider {
    /**
     * 获取一个连接
     * @return 连接
     */
    Channel getChannel();
}
