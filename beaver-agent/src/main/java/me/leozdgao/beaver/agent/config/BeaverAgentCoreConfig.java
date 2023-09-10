package me.leozdgao.beaver.agent.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.leozdgao.beaver.spi.BeaverProperties;
import me.leozdgao.beaver.worker.config.WorkerModule;
import me.leozdgao.beaver.worker.sd.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BeaverAgentCoreConfig {
    @Bean
    public Injector beaverInjector() throws IOException {
        BeaverProperties properties = BeaverProperties.loadFromFile();
        return Guice.createInjector(
                new WorkerModule(properties)
        );
    }

    @Bean
    public ServiceRegistry serviceRegistry(Injector injector) {
        return injector.getInstance(ServiceRegistry.class);
    };
}
