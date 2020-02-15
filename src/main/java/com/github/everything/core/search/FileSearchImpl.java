package com.github.everything.core.search;

import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务层
 */
public class FileSearchImpl implements FileSearch {

    /**
     * final初始化 1.直接初始化 2.构造初始化 3.代码块初始化
     */
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public List<Thing> search(Condition condition) {
        if(condition == null){
            return new ArrayList<>();
        }
        return this.fileIndexDao.search(condition);
    }

}