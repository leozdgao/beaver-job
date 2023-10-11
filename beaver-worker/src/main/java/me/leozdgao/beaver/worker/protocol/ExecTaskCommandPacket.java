package me.leozdgao.beaver.worker.protocol;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 执行任务包
 * @author leozdgao
 */
@Data
@Builder
public class ExecTaskCommandPacket extends Packet {
    private Long taskId;
    private String taskType;
    private Map<String, Object> payload;
    private Map<String, Object> extra;

    @Override
    public Byte getCommand() {
        return Command.TASK_EXEC;
    }
}
