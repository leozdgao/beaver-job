package me.leozdgao.beaver.infrastructure.config;

import com.google.inject.AbstractModule;
import me.leozdgao.beaver.infrastructure.TaskPersistenceServiceImpl;
import me.leozdgao.beaver.spi.TaskPersistenceService;

/**
 * 任务中心底层数据持久化相关配置
 * @author zhendong.gzd
 */
public class PersistenceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskPersistenceService.class).to(TaskPersistenceServiceImpl.class);
    }
}
