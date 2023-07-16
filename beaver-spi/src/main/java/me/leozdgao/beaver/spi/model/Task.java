package me.leozdgao.beaver.spi.model;

import lombok.Data;

import java.util.Map;

/**
 * @author leozdgao
 */
@Data
public class Task {
    private String type;
    private TaskStatus status;
    private Map<String, Object> payload;
    private Map<String, Object> ext;
}
