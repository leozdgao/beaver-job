package me.leozdgao.beaver.worker.protocol;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.worker.utils.TraceUtils;

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

    private  final TaskPersistenceCommandService taskPersistenceCommandService;

    @Inject
    public TrustedSender(TaskPersistenceCommandService taskPersistenceCommandService) {
        this.taskPersistenceCommandService = taskPersistenceCommandService;
    }


    /**
     * 发送直到接收方相应ack
     * @param channel 连接通道
     * @param packet 包
     * @param timeout 超时时间
     * @return 接收方是否已接收
     */
    public Promise<Boolean> sendUntilResponse(Channel channel, Packet packet, long timeout) {
        String traceId = TraceUtils.getTraceId();
        packet.setTraceId(traceId);

        EventLoop eventLoop = channel.eventLoop();
        Promise<Boolean> promise = eventLoop.newPromise();
        promises.put(traceId, promise);

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

    public SimpleChannelInboundHandler<TracingResponsePacket> tracingHandler() {
        return new TaskReceptionHandler();
    }

    public SimpleChannelInboundHandler<TaskResponsePacket> responseHandler() {
        return new TaskExecutionResponseHandler();
    }

    public class TaskExecutionResponseHandler extends SimpleChannelInboundHandler<TaskResponsePacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TaskResponsePacket msg) throws Exception {
            if (msg.isSuccess()) {
                taskPersistenceCommandService.taskSuccess(msg.getTaskId(), msg.getResult());
            } else {
                taskPersistenceCommandService.taskFailed(msg.getTaskId(), new RuntimeException(msg.getMessage()));
            }
        }
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
            if (promise == null || promise.isDone()) {
                // promise 已经被设置结束，忽略
                // TODO: 打日志
                return;
            }

            if (msg.isAccepted()) {
                promise.setSuccess(true);
            } else {
                promise.setFailure(new WorkerResponseException(traceId, msg.getCode(), msg.getMessage()));
            }

            // 完成了的进行清理
            promises.remove(traceId);
        }
    }
}
