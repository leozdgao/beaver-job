package me.leozdgao.beaver.worker.protocol;

import lombok.Data;

@Data
public abstract class TracingResponsePacket extends Packet {
    private boolean accepted;
    private String code;
    private String message;
}
