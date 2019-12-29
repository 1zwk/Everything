package com.github.everything.core.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *文件类型细分
 */
public enum FileType {
    //图片
    IMG("png","jpeg","jpg","gif"),
    //文档
    DOC("ppt","pptx","doc","pdf","docx"),
    //二进制
    BIN("exe","sh","jar","msi"),
    //归档
    ARCHIVE("zip","rar"),
    //其他
    OTHER("*");

    /**
     * 对应的文件类型的扩展名
     */
    private Set<String> extend = new HashSet<>();

    FileType(String... extend){
        this.extend.addAll(Arrays.asList(extend));
    }


    /**
     * 根据文件扩展名获取文件类型
     */
    public static FileType lookup(String extend){
        for(FileType ft : FileType.values()){
            if(ft.extend.contains(extend)){
                return ft;
            }
        }
        return FileType.OTHER;
    }

    /**
     * 根据文件类型名（String）获取文件类型
     */
    public static FileType lookupByName(String name){
        for(FileType ft : FileType.values()){
            if(ft.name().equals(name)){
                return ft;
            }
        }
        return FileType.OTHER;
    }

    public static void main(String[] args) {
        System.out.println(lookup("ppt"));
        System.out.println(lookup("doc"));
        System.out.println(lookup("dsas"));
    }

}
