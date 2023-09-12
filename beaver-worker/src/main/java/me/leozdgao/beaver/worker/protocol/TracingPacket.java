package me.leozdgao.beaver.worker.protocol;

import java.util.UUID;

/**
 * @author leozdgao
 */
public abstract class TracingPacket extends Packet {
    private final String traceId;

    public TracingPacket() {
        this.traceId = UUID.randomUUID().toString();
    }

    public TracingPacket(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}
