package com.github.everything.core.interceptor.impl;

import com.github.everything.core.common.FileConvertThing;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.index.FileScan;
import com.github.everything.core.index.impl.FileScanImpl;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.model.Thing;

import java.io.File;

//向数据库中插入遍历好的文件数据
public class FileIndexInterceptor implements FileInterceptor {
    private  final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        fileIndexDao.insert(thing);
    }
}
