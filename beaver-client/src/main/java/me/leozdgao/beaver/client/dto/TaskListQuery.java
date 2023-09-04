package me.leozdgao.beaver.client.dto;

import lombok.Data;

/**
 * @author leozdgao
 */
@Data
public class TaskListQuery {
    private String taskType;
    private Integer taskStatus;
    private Long pageIndex = 1L;
    private Long pageSize = 20L;
}
