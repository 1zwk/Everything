package com.github.everything.core.search;

import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;

import java.util.List;

//? 为何要先写一个接口在实现，直接创建搜索类会怎么样
public interface FileSearch {
    /**
     * 根据condition进行数据库的检索
     * @param condition
     * @return
     */

     List<Thing> search(Condition condition);
}
