package me.leozdgao.beaver.worker.protocol;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 任务结果相应包
 * @author leozdgao
 */
@Data
@Builder
public class TaskResponsePacket extends Packet {
    private Long taskId;
    private boolean isSuccess;
    private Map<String, Object> result;
    private String code;
    private String message;

    @Override
    public Byte getCommand() {
        return Command.TASK_RESPONSE;
    }
}
