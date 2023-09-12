package me.leozdgao.beaver.worker.protocol;

import lombok.Builder;
import lombok.Data;

/**
 * 任务接收确认包
 * @author leozdgao
 */
@Data
@Builder
public class TaskReceivedPacket extends Packet {
    private Long taskId;
    private boolean isRunning;
    private String code;
    private String message;

    @Override
    public Byte getCommand() {
        return Command.TASK_RECEIVED;
    }
}
