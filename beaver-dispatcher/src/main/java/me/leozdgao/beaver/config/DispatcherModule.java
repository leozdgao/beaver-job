package me.leozdgao.beaver.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import jakarta.inject.Qualifier;
import me.leozdgao.beaver.dispatcher.TaskEvent;
import me.leozdgao.beaver.dispatcher.TaskEventLauncher;

/**
 * 调度器依赖注入模块配置
 * @author zhendong.gzd
 */
public class DispatcherModule extends AbstractModule {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RingBufferSize {}

    /**
     * 用于阻塞队列的 RingBuffer 最大长度
     * @return 长度
     */
    @Provides
    @RingBufferSize
    public static int ringBufferSize() {
        return 1024;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<WorkHandler<TaskEvent>>() {})
            .to(new TypeLiteral<TaskEventLauncher>() {});
    }
}
