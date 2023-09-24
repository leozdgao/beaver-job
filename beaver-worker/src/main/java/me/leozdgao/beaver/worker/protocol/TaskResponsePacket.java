package me.leozdgao.beaver.worker.protocol;

import lombok.Builder;
import lombok.Data;
import me.leozdgao.beaver.client.Response;

import java.util.Map;

/**
 * 任务结果相应包
 * @author leozdgao
 */
@Data
@Builder
public class TaskResponsePacket extends Packet {
    private Long taskId;
    private String traceId;
    private boolean isSuccess;
    private String result;
    private String code;
    private String message;

    @Override
    public Byte getCommand() {
        return Command.TASK_RESPONSE;
    }

    public static TaskResponsePacket buildSuccess(ExecTaskCommandPacket pkt) {
        return buildSuccess(pkt, null);
    }

    public static TaskResponsePacket buildSuccess(ExecTaskCommandPacket pkt, String result) {
        return TaskResponsePacket.builder()
                .taskId(pkt.getTaskId())
                .traceId(pkt.getTraceId())
                .isSuccess(true)
                .result(result)
                .code("OK")
                .message("success")
                .build();
    }

    public static TaskResponsePacket buildFailure(ExecTaskCommandPacket pkt, String message) {
        return buildFailure(pkt, "SYS_ERROR", message);
    }

    public static TaskResponsePacket buildFailure(ExecTaskCommandPacket pkt, String code, String message) {
        return TaskResponsePacket.builder()
                .taskId(pkt.getTaskId())
                .traceId(pkt.getTraceId())
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }
}
