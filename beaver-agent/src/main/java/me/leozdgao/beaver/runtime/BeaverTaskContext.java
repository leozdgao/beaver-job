package me.leozdgao.beaver.runtime;

import lombok.Data;

import java.util.Map;

/**
 * @author leozdgao
 */
@Data
public class BeaverTaskContext {
    private Long taskId;
    private String traceId;
    private Map<String, Object> parameters;
}
