package me.leozdgao.beaver.runtime;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.Channel;
import me.leozdgao.beaver.agent.service.ChannelProvider;
import me.leozdgao.beaver.worker.protocol.ExecTaskCommandPacket;
import me.leozdgao.beaver.worker.protocol.TaskResponsePacket;

import java.lang.reflect.Constructor;

/**
 * 临时，后续改成自定义实现的 ExecutorService
 * @author leozdgao
 */
public class BeaverTaskExecutor implements Runnable {
    private final ExecTaskCommandPacket pkt;
    private final ChannelProvider channelProvider;
    private final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

    public BeaverTaskExecutor(ExecTaskCommandPacket pkt, ChannelProvider channelProvider) {
        this.pkt = pkt;
        this.channelProvider = channelProvider;
    }

    private void reply(TaskResponsePacket responsePacket) {
        Channel channel = channelProvider.getChannel();

        if (channel != null) {
            channel.writeAndFlush(responsePacket);
        }

        // TODO: 放入延时队列，尝试重试
    }

    @Override
    public void run() {
        // FIXME: 一下代码应该交给一个线程池去执行，也可以考虑用 Disruptor 去实现
        // 测试阶段暂时写死，后续应该是配置化的
        String taskClassName = "me.leozdgao.beaver.runtime.tasks.MagicStringBuilderTask";

        BeaverTask<?> taskInstance;
        try {
            Class<?> klass = Class.forName(taskClassName);
            // 判断 klass 是否实现了 BeaverTask 接口
            if (BeaverTask.class.isAssignableFrom(klass)) {
                Constructor<? extends BeaverTask> constructor = (Constructor<? extends BeaverTask>) klass.getDeclaredConstructor();
                taskInstance = constructor.newInstance();
            } else {
                reply(TaskResponsePacket.buildFailure(
                        pkt, "TARGET_EXECUTE_CLASS_LOAD_FAIL", "找不到执行类，任务失败"));
                return;
            }
        } catch (Exception e) {
            reply(TaskResponsePacket.buildFailure(
                    pkt, "TARGET_EXECUTE_CLASS_LOAD_FAIL", String.format("找不到执行类，任务失败：%s", e)));
            // 找不到执行类，任务失败
            return;
        }

        try {
            BeaverTaskContext context = new BeaverTaskContext();
            context.setTaskId(pkt.getTaskId());
            context.setTraceId(pkt.getTraceId());
            context.setParameters(pkt.getPayload());
            // TODO: 超时中断
            Object result = taskInstance.execute(context);

            // 执行成功
            reply(TaskResponsePacket.buildSuccess(pkt, gson.toJson(result)));
        } catch (Exception e) {
            // 任务正常执行异常
            reply(TaskResponsePacket.buildFailure(
                    pkt, "TASK_EXECUTE_EXCEPTION", String.format("任务执行异常：%s", e)));
        }
    }
}
