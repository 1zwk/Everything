package com.github.everything.core.model;

import lombok.Data;

import java.util.List;

/**
 * 文件属性信息索引之后的记录
 */

@Data //生成get set toString
public class Thing {

    //设置属性最好使用包装类

    /**
     * 文件id
     */
    private Integer id;

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


    /**
     * 文件大小
     */
    private String FileSize;


    /**
     * 最后修改时间
     */
    private String last_modified;
}
