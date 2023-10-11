package me.leozdgao.beaver.dispatcher;

import com.alibaba.arms.tracing.Tracer;
import com.lmax.disruptor.WorkHandler;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.leozdgao.beaver.spi.TaskPersistenceCommandService;
import me.leozdgao.beaver.spi.model.Task;
import me.leozdgao.beaver.spi.model.TaskTransitionEvent;
import me.leozdgao.beaver.worker.Worker;
import me.leozdgao.beaver.worker.WorkerManager;
import me.leozdgao.beaver.worker.protocol.WorkerResponseException;
import me.leozdgao.beaver.worker.utils.TraceUtils;
import org.slf4j.MDC;

/**
 * 收到任务入队请求，开始执行调度逻辑
 * @author leozdgao
 */
@Slf4j
public class TaskEventLauncher implements WorkHandler<TaskEvent> {
    private final WorkerManager workerManager;
    private final TaskPersistenceCommandService taskPersistenceCommandService;

    @Inject
    public TaskEventLauncher(
            WorkerManager workerManager,
            TaskPersistenceCommandService taskPersistenceCommandService) {
        this.workerManager = workerManager;
        this.taskPersistenceCommandService = taskPersistenceCommandService;

        workerManager.start();
    }

    public void close() {
        workerManager.close();
    }

    @Override
    public void onEvent(TaskEvent event) {
        String traceId = event.getTraceId();

        if (traceId != null) {
            TraceUtils.setTraceId(traceId);
        }

        log.info("Handling Event, {}", event);

        try {
            // 根据动态负载均衡器，获取可接受任务的 worker
            // 建立与 worker 的连接，发送任务
            // - 如果获取 worker 失败或者连接 worker 失败，重新放回等待队列（增加一个延时），多次失败的，直接设置任务失败
            // 发送任务成功，更新任务数据库状态为 RUNNING
            Task task = event.getTask();
            Worker worker = workerManager.getNextWorker(task.getScope());

            if (worker == null) {
                // FIXME: worker 为空，应该先重试
                Exception exp = new RuntimeException("没有可用的 worker，任务无法下发");
                log.warn(exp.toString());
                taskPersistenceCommandService.taskFailed(task.getId(), exp);

                return;
            }

            workerManager.connect(worker, () ->
                workerManager.sendTask(worker, task, () -> {
                    taskPersistenceCommandService.updateTaskStatus(task.getId(), TaskTransitionEvent.DISPATCH);
                }, (e) -> {
                    // worker 拒绝执行，直接失败
                    if (e instanceof WorkerResponseException) {
                        WorkerResponseException exp = (WorkerResponseException) e;
                        taskPersistenceCommandService.taskFailed(task.getId(),
                                new RuntimeException(
                                        String.format("Worker 拒绝执行：%s %s", exp.getCode(), exp.getMessage())));
                    } else {
                        // 发送指令失败，将任务重新放回等待队列
                    }
                })
            , (e) -> {
                // 连接失败，将任务重新放回等待队列
            });
        } catch (Exception e) {
            // 任意失败，将任务重新放回等待队列
            e.printStackTrace();
        }
    }
}
