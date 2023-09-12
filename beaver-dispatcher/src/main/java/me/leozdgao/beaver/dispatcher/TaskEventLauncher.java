package me.leozdgao.beaver.dispatcher;

import com.lmax.disruptor.EventHandler;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskStatus;
import me.leozdgao.beaver.worker.Worker;
import me.leozdgao.beaver.worker.WorkerManager;

/**
 * 收到任务入队请求，开始执行调度逻辑
 * @author leozdgao
 */
@Slf4j
public class TaskEventLauncher implements EventHandler<TaskEvent> {
    private final WorkerManager workerManager;
    private final TaskPersistenceCommandService taskPersistenceCommandService;

    @Inject
    public TaskEventLauncher(WorkerManager workerManager, TaskPersistenceCommandService taskPersistenceCommandService) {
        this.workerManager = workerManager;
        this.taskPersistenceCommandService = taskPersistenceCommandService;

        workerManager.start();
    }

    public void close() {
        workerManager.close();
    }

    public void onEvent(TaskEvent event, long l, boolean b) throws Exception {
        try {
            // 根据动态负载均衡器，获取可接受任务的 worker
            // 建立与 worker 的连接，发送任务
            // - 如果获取 worker 失败或者连接 worker 失败，重新放回等待队列（增加一个延时），多次失败的，直接设置任务失败
            // 发送任务成功，更新任务数据库状态为 RUNNING
            Task task = event.getTask();
            Worker worker = workerManager.getNextWorker(task.getScope());

            if (worker == null) {
                taskPersistenceCommandService.updateTaskStatus(task, TaskStatus.FAILED);
                return;
            }

            workerManager.connect(worker, () -> {
                workerManager.sendTask(worker, task, () -> {
                    taskPersistenceCommandService.updateTaskStatus(task, TaskStatus.RUNNING);
                }, (e) -> {
                    // 发送指令失败，将任务重新放回等待队列
                });
            }, (e) -> {
                // 连接失败，将任务重新放回等待队列
            });
        } catch (Exception e) {
            // 任意失败，将任务重新放回等待队列
            e.printStackTrace();
        }

        Thread.sleep(500);

    }
}
