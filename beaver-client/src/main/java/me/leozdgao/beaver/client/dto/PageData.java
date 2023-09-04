package me.leozdgao.beaver.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageData<T> {
    private List<T> list;
    private Long pageIndex;
    private Long pageSize;
    private Long count;
}
