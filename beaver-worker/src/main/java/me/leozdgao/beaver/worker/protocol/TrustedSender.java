package me.leozdgao.beaver.worker.protocol;

import io.netty.channel.Channel;

import java.util.concurrent.Future;

/**
 * 基于长连接的可信协议发送器，支持：
 * - 等待直到接收方返回 ack
 * - 超时重试机制
 * @author leozdgao
 */
public class TrustedSender {
    /**
     * 发送直到接收方相应ack
     * @param channel 连接通道
     * @param packet 包
     * @return 接收方的响应包
     */
    Future<Packet> sendUntilResponse(Channel channel, TracingPacket packet) {
        return null;
    }

    /**
     * 发送，不管接受情况
     * @param channel 连接通道
     * @param packet 包
     */
    void send(Channel channel, Packet packet) {
        channel.writeAndFlush(packet);
    }
}
