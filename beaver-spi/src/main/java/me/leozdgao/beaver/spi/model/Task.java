package me.leozdgao.beaver.spi.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author leozdgao
 */
@Data
@Builder
public class Task {
    private Long id;
    private String type;
    private String scope;
    private TaskStatus status;
    private Map<String, Object> payload;
    private Map<String, Object> result;
    private Map<String, Object> ext;
}
