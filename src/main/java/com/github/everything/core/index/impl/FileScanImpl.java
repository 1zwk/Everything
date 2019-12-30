package com.github.everything.core.index.impl;

import com.github.everything.config.EverythingPlusConfig;
import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.everything.core.index.FileScan;
import com.github.everything.core.interceptor.FileInterceptor;
import com.github.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.everything.core.interceptor.impl.FilePrintInterceptor;
import com.github.everything.core.model.Thing;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件转换类：
 */
public class FileScanImpl implements FileScan {

    private EverythingPlusConfig config = EverythingPlusConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();
    //递归遍历文件并保存起来
    @Override
    public void index(String path) {

        File file = new File(path);

        if(file.exists()){
            if(file.isFile()){
                //判断父目录是否包含excludePath,排除不想要的目录文件
                // 例如：D：\a\b\abd.pdf  ——> D:\a
                if(config.getExcludePath().contains(file.getParent())){
                    return; //为了加快排除时间
                }
            }else{
                if(config.getExcludePath().contains(path)) {
                    return;
                }else {
                    File[] files = file.listFiles();
                    if (files != null) {//不为空才可以遍历
                        for (File f : files) {
                            index(f.getAbsolutePath());//递归
                        }
                    }
                }
            }

            for(FileInterceptor interceptor : this.interceptors){
                interceptor.apply(file);
            }
        }
    }

    @Override
    public void interceptor(FileInterceptor fileInterceptor) {
        this.interceptors.add(fileInterceptor);
    }



}
