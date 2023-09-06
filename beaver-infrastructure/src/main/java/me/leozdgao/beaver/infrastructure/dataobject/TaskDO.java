package me.leozdgao.beaver.infrastructure.dataobject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhendong.gzd
 */
@Getter
@Setter
@ToString(callSuper = true)
public class TaskDO extends BaseDO {
    /**
     * 任务类型
     */
    private String type;
    /**
     * 域
     */
    private String scope;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 任务输入
     */
    private String payload;
    /**
     * 任务执行结果
     */
    private String result;
    /**
     * 任务透传字段
     */
    private String extra;
}
