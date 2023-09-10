package me.leozdgao.beaver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.leozdgao.beaver.config.DispatcherModule;
import me.leozdgao.beaver.spi.BeaverProperties;
import me.leozdgao.beaver.infrastructure.config.JsonModule;
import me.leozdgao.beaver.infrastructure.config.PersistenceModule;
import me.leozdgao.beaver.spi.TaskPersistenceQueryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author zhendong.gzd
 */
@SpringBootApplication(scanBasePackages = {"me.leozdgao.beaver.adaptor.*"})
public class Application {
    public static void main(String[] args) throws Exception {
        // 正常而言这里应该是启动 Web 应用
        SpringApplication.run(Application.class, args);

        // demo();
    }

    private static void demo() throws IOException {
        BeaverProperties properties = BeaverProperties.loadFromFile();
        Injector injector = Guice.createInjector(
            new DispatcherModule(),
            new PersistenceModule(properties),
            new JsonModule()
        );

        TaskPersistenceQueryService taskPersistenceQueryService =
                injector.getInstance(TaskPersistenceQueryService.class);

        // TaskConverter taskConverter = injector.getInstance(TaskConverter.class);

        // TaskDispatcher taskDispatcher = injector.getInstance(TaskDispatcher.class);
        // taskDispatcher.init();
        //
        // TaskMapper taskMapper = injector.getInstance(TaskMapper.class);
        // List<TaskDO> lists = taskMapper.findTaskPage(null);
        // System.out.println(lists);

        //for (int i = 0; i < 10; i++) {
        //    Task task = new Task();
        //    taskDispatcher.accept(task);
        //}
        //
        //Thread.sleep(5000);
    }
}