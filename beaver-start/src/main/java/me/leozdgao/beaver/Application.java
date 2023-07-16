package me.leozdgao.beaver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.leozdgao.beaver.dispatcher.TaskDispatcher;
import me.leozdgao.beaver.dispatcher.config.DispatcherModule;
import me.leozdgao.beaver.infrastructure.config.PersistenceModule;
import me.leozdgao.beaver.spi.model.Task;

/**
 * @author zhendong.gzd
 */
public class Application {
    public static void main(String[] args) throws InterruptedException {
        Injector injector = Guice.createInjector(
            new DispatcherModule(),
            new PersistenceModule()
        );
        TaskDispatcher taskDispatcher = injector.getInstance(TaskDispatcher.class);
        taskDispatcher.init();

        //for (int i = 0; i < 10; i++) {
        //    Task task = new Task();
        //    taskDispatcher.accept(task);
        //}
        //
        //Thread.sleep(5000);
    }
}