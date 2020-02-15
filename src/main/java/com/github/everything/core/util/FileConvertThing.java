package com.github.everything.core.util;

import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 辅助工具类：把遍历的文件file类转换为Thing类
 *
 * 因为是内部工具类，并不想外界使用或者new对象，所以通过final和私有构造方法把这个类封闭起来
 */
public final class FileConvertThing {
    private static AtomicInteger i = new AtomicInteger(1);



    private FileConvertThing(){}


    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setId(i.getAndIncrement());
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        thing.setFileSize(countFileSize(file));
        thing.setLast_modified(String.valueOf(file.lastModified()));

        return thing;
    }




    //计算文件深度
    private static int computeFileDepth(File file){
        String[] segments = file.getAbsolutePath().split("\\\\");//没懂为什么是四个\\
        return  segments.length;
    }

    //计算文件类型
    private static FileType computeFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        //保证文件有后缀并且合乎规范，例如：name.
        if(index != -1 && index < fileName.length()-1){//保证存在index并且不在最后一位（防止下标越界）。
            String extend = fileName.substring(index+1);
            return FileType.lookup(extend);
        }else {
            return FileType.OTHER;
        }
    }

    private static String[] unit = {"B","KB","MB","GB"};
    //计算文件大小
    private static String countFileSize(File file){

        long length = file.length();
        int indexUnit = 0;
        long remainder = 0;
        while(length > 1024){
            remainder = length % 1024;
            length = length/1024;
            indexUnit++;
        }

        return length + "." + remainder +unit[indexUnit];
    }



}
