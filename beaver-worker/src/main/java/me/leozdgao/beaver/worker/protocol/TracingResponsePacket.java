package me.leozdgao.beaver.worker.protocol;

import lombok.Data;

@Data
public abstract class TracingResponsePacket extends TracingPacket {
    private boolean accepted;
    private String code;
    private String message;

    public TracingResponsePacket(String traceId) {
        super(traceId);
    }
}
