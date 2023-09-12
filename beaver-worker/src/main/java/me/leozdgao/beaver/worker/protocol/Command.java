package me.leozdgao.beaver.worker.protocol;

/**
 * @author leozdgao
 */
public interface Command {
    Byte TASK_EXEC = 10;
    Byte TASK_RECEIVED = 20;
    Byte TASK_RESPONSE = 21;

    static Class<? extends Packet> getRequestType(byte command) {
        if (TASK_EXEC.equals(command)) {
            return ExecTaskCommandPacket.class;
        } else if (TASK_RECEIVED.equals(command)) {
            return TaskReceivedPacket.class;
        } else if (TASK_RESPONSE.equals(command)) {
            return TaskResponsePacket.class;
        }

        return null;
    }
}
