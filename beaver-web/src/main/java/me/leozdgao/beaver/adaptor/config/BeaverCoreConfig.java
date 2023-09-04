package me.leozdgao.beaver.adaptor.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.leozdgao.beaver.dispatcher.TaskDispatcher;
import me.leozdgao.beaver.dispatcher.config.DispatcherModule;
import me.leozdgao.beaver.infrastructure.BeaverProperties;
import me.leozdgao.beaver.infrastructure.config.PersistenceModule;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Beaver调度器初始化
 * @author leozdgao
 */
@Configuration
public class BeaverCoreConfig {
    @Bean
    public Injector beaverInjector() throws IOException {
        BeaverProperties properties = BeaverProperties.loadFromFile();
        return Guice.createInjector(
                new DispatcherModule(),
                new PersistenceModule(properties)
        );
    }

    @Bean
    public TaskDispatcher taskDispatcher(Injector injector) {
        TaskDispatcher taskDispatcher = injector.getInstance(TaskDispatcher.class);
        taskDispatcher.init();

        return taskDispatcher;
    }

    @Bean
    public TaskPersistenceCommandService taskPersistenceCommandService(Injector injector) {
        return injector.getInstance(TaskPersistenceCommandService.class);
    }
}