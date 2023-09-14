package me.leozdgao.beaver.worker.protocol;

import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 基于长连接的可信协议发送器，支持：
 * - 等待直到接收方返回 ack
 * - 超时重试机制
 * @author leozdgao
 */
@Singleton
public class TrustedSender {
    private final Map<String, Promise<Boolean>> promises = new ConcurrentHashMap<>();

    /**
     * 发送直到接收方相应ack
     * @param channel 连接通道
     * @param packet 包
     * @param timeout 超时时间
     * @return 接收方是否已接收
     */
    public Promise<Boolean> sendUntilResponse(Channel channel, TracingPacket packet, long timeout) {
        EventLoop eventLoop = channel.eventLoop();
        Promise<Boolean> promise = eventLoop.newPromise();
        promises.put(packet.getTraceId(), promise);

        // 发送
        eventLoop.execute(() -> {
            channel.writeAndFlush(packet).addListener(f -> {
                // 发送失败直接设置为失败
                if (!f.isSuccess()) {
                    promise.setFailure(f.cause());
                }
            });
        });
        // 超时
        eventLoop.schedule(() -> {
            promise.setFailure(new TimeoutException());
        }, timeout, TimeUnit.MICROSECONDS);
        return promise;
    }

    /**
     * 发送，不管接受情况
     * @param channel 连接通道
     * @param packet 包
     */
    public void send(Channel channel, Packet packet) {
        channel.writeAndFlush(packet);
    }

    public SimpleChannelInboundHandler<TracingResponsePacket> pipelineHandler() {
        return new TaskReceptionHandler();
    }

    public class TaskReceptionHandler extends SimpleChannelInboundHandler<TracingResponsePacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TracingResponsePacket msg) throws Exception {
            String traceId = msg.getTraceId();
            if (traceId == null) {
                // 无效 ack 包
                return;
            }

            Promise<Boolean> promise = promises.get(traceId);
            if (promise.isDone()) {
                // promise 已经被设置结束，忽略
                // TODO: 打日志
                return;
            }

            if (msg.isAccepted()) {
                promise.setSuccess(true);
            } else {
                promise.setFailure(new RuntimeException(msg.getMessage()));
            }
        }
    }
}
