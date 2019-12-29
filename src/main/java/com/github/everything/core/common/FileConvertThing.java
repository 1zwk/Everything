package com.github.everything.core.common;

import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类：把遍历的文件file类转换为Thing类
 *
 * 因为是内部工具类，并不想外界使用或者new对象，所以通过final和私有构造方法把这个类封闭起来
 */
public final class FileConvertThing {

    private FileConvertThing(){}

    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        return thing;
    }

    //计算文件深度
    private static int computeFileDepth(File file){
        int depth = 0;
        String[] segments = file.getAbsolutePath().split(File.separator);
        depth = segments.length;
        return depth;
    }

    //计算文件类型
    private static FileType computeFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        //例如：name.
        if(index != -1 && index < fileName.length()-1){//保证存在index并且不在最后一位（防止下标越界）。
            String extend = fileName.substring(index+1);
            return FileType.lookup(extend);
        }else {
            return FileType.OTHER;
        }
    }




}