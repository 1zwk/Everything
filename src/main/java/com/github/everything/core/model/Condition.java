package com.github.everything.core.model;

import lombok.Data;

/**
 * 输入的搜索条件
 */
@Data
public class Condition {
    private String name;

    private String fileType;

    private Integer limit;

    /**
     * 检索结果文件信息按depth排序
     * true ->升序
     * false -> 降序
     */
    private Boolean orderByAsc;
}
