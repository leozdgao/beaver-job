package me.leozdgao.beaver.worker.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * 任务接收确认包
 * @author leozdgao
 */
@Getter
@Setter
public class TaskReceivedPacket extends TracingResponsePacket {
    private Long taskId;

    @Override
    public Byte getCommand() {
        return Command.TASK_RECEIVED;
    }
}
