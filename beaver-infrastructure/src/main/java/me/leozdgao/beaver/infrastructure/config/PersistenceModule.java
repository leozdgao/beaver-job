package me.leozdgao.beaver.infrastructure.config;

import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.inject.Singleton;
import me.leozdgao.beaver.spi.BeaverProperties;
import me.leozdgao.beaver.infrastructure.SqlSessionTemplate;
import me.leozdgao.beaver.infrastructure.impl.TaskPersistenceQueryServiceImpl;
import me.leozdgao.beaver.infrastructure.impl.TaskSinglePersistenceServiceImpl;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import me.leozdgao.beaver.spi.model.TaskTransitionEvent;
import me.leozdgao.beaver.spi.model.TaskStatus;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.sql.PreparedStatement;

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

    @Provides
    @Singleton
    public StateMachine<TaskStatus, TaskTransitionEvent, Object> taskStateMachine() {
        StateMachineBuilder<TaskStatus, TaskTransitionEvent, Object> builder = StateMachineBuilderFactory.create();
        // 从创建到入队列准备执行
        builder.externalTransition()
                .from(TaskStatus.REQUESTING)
                .to(TaskStatus.WAITING)
                .on(TaskTransitionEvent.PLAN);
        // 任务调度失败，重新回到待安排的状态
        builder.externalTransition()
                .from(TaskStatus.WAITING)
                .to(TaskStatus.REQUESTING)
                .on(TaskTransitionEvent.REARRANGE);
        // 1. 任务调度失败，无法安排执行
        // 2. 任务调度成功，ack 返回前已经执行完成且结果相应包先到
        builder.externalTransition()
                .from(TaskStatus.WAITING)
                .to(TaskStatus.FAILED)
                .on(TaskTransitionEvent.FAIL);
        // 任务调度成功，ack 返回前已经执行完成且结果相应包先到
        builder.externalTransition()
                .from(TaskStatus.WAITING)
                .to(TaskStatus.SUCCESS)
                .on(TaskTransitionEvent.SUCCESS);
        // 任务完成分配下发，进入执行中状态
        builder.externalTransition()
                .from(TaskStatus.WAITING)
                .to(TaskStatus.RUNNING)
                .on(TaskTransitionEvent.DISPATCH);
        // 任务成功
        builder.externalTransition()
                .from(TaskStatus.RUNNING)
                .to(TaskStatus.SUCCESS)
                .on(TaskTransitionEvent.SUCCESS);
        // 任务失败
        builder.externalTransition()
                .from(TaskStatus.RUNNING)
                .to(TaskStatus.FAILED)
                .on(TaskTransitionEvent.FAIL);

        return builder.build("taskStateMachine");
    }
}
