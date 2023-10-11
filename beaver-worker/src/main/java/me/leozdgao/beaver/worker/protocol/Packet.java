package me.leozdgao.beaver.worker.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author leozdgao
 */
public abstract class Packet {
    private final Byte version = 1;

    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 返回当前指令编码
     * @return byte
     */
    public abstract Byte getCommand();

    public String getTraceId() {
        Object val = getAttribute("x-traceId");
        return val == null ? null : val.toString();
    }

    public String getSpanId() {
        Object val = getAttribute("x-spanId");
        return val == null ? null : val.toString();
    }

    public void setTraceId(String value) {
        setAttribute("x-traceId", value);
    }

    public void setSpanId(String value) {
        setAttribute("x-spanId", value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
