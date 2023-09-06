package me.leozdgao.beaver.infrastructure.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.inject.Singleton;
import me.leozdgao.beaver.infrastructure.BeaverProperties;
import me.leozdgao.beaver.infrastructure.SqlSessionTemplate;
import me.leozdgao.beaver.infrastructure.impl.TaskPersistenceQueryServiceImpl;
import me.leozdgao.beaver.infrastructure.impl.TaskSinglePersistenceServiceImpl;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * 任务中心底层数据持久化相关配置
 * @author zhendong.gzd
 */
public class PersistenceModule extends AbstractModule {
    private BeaverProperties properties;

    public PersistenceModule(BeaverProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {
        bind(TaskPersistenceCommandService.class).to(TaskSinglePersistenceServiceImpl.class);
        bind(TaskPersistenceQueryService.class).to(TaskPersistenceQueryServiceImpl.class);
    }

    @Provides
    @Singleton
    public SqlSessionFactory provideSqlSessionFactory() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));

        Environment environment = new Environment("env", new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TaskMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Provides
    @Singleton
    public SqlSessionTemplate provideSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Provides
    @Singleton
    public TaskMapper provideTaskMapper(SqlSessionTemplate sqlSession) {
        // 返回一个单例的 TaskMapperProxy，但每次调用相关方法都会创建一个新的 SqlSession
        return sqlSession.getMapper(TaskMapper.class);
    }
}
