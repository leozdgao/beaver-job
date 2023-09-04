package me.leozdgao.beaver.client.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author leozdgao
 */
@Data
public class TaskCreationCommand {
    private String taskType;
    private Map<String, Object> payload;
    private Map<String, Object> extra;
}
