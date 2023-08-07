package me.leozdgao.beaver;

import java.io.IOException;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.leozdgao.beaver.dispatcher.TaskDispatcher;
import me.leozdgao.beaver.dispatcher.config.DispatcherModule;
import me.leozdgao.beaver.infrastructure.BeaverProperties;
import me.leozdgao.beaver.infrastructure.config.PersistenceModule;
import me.leozdgao.beaver.infrastructure.dataobject.TaskDO;
import me.leozdgao.beaver.infrastructure.mapper.TaskMapper;
import me.leozdgao.beaver.spi.model.Task;

/**
 * @author zhendong.gzd
 */
public class Application {
    public static void main(String[] args) throws Exception {
        // 正常而言这里应该是启动 Web 应用

        demo();
    }

    private static void demo() throws IOException {
        BeaverProperties properties = BeaverProperties.loadFromFile();
        Injector injector = Guice.createInjector(
            new DispatcherModule(),
            new PersistenceModule(properties)
        );
        TaskDispatcher taskDispatcher = injector.getInstance(TaskDispatcher.class);
        taskDispatcher.init();

        TaskMapper taskMapper = injector.getInstance(TaskMapper.class);
        List<TaskDO> lists = taskMapper.findAllTask();
        System.out.println(lists);

        //for (int i = 0; i < 10; i++) {
        //    Task task = new Task();
        //    taskDispatcher.accept(task);
        //}
        //
        //Thread.sleep(5000);
    }
}