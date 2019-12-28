package com.github.everything.core.model;

import lombok.Data;

/**
 * 文件属性信息索引之后的记录
 */

@Data //生成get set toString
public class Thing {

    /**
     * 文件名称（保留名称）
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件深度
     */
    private Integer depth;

    /**
     * 文件类型
     */
    private FileType fileType;

}
