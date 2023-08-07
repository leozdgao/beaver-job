package me.leozdgao.beaver.infrastructure.dataobject;

import java.util.Date;

import lombok.Data;

@Data
public class BaseDO {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
}
