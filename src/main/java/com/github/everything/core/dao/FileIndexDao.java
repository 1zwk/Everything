package com.github.everything.core.dao;

import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;

import java.util.List;

/**
 * 业务层访问数据库的CIUD（增删改查）
 */
public interface FileIndexDao {
    /**
     * 根据condition进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

    /**
     * 插入数据Thing
     * @param thing
     */
    void insert(Thing thing);


}
