package com.github.everything.config;

import com.github.everything.core.search.FileSearch;
import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 配置类:对一些特定的文件或者目录进行排除，换句话说，搜索想要的，不搜索不想要的。
 */
@Getter
public class EverythingPlusConfig {

    private static volatile EverythingPlusConfig config;

    //私有这个类
    private EverythingPlusConfig(){};

    //建立索引文件的路径
    private Set<String> includePath = new HashSet<>();

    //排除索引文件的路径
    private Set<String> excludePath = new HashSet<>();

    public static EverythingPlusConfig getInstance(){
        if(config == null){
            synchronized (EverythingPlusConfig.class){
                if(config == null){
                    config = new EverythingPlusConfig();

                    //1.获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();
                    /**
                     * 遍历的目录
                     */
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.includePath.add(path.toString()));
                    /**
                     * 排除的目录
                     * Windows ： C:\Program Files、 C:\Program Files (x86)、 C:\ProgramData
                     * Linux ：/tmp /stc
                     */
                    //获取系统名称
                    String osname = System.getProperty("os.name");
                    if(osname.startsWith("Windows")){
                        config.getExcludePath().add("C:\\Windows");//?
                        config.getExcludePath().add("C:\\Program Files");
                        config.getExcludePath().add("C:\\Program Files (x86)");
                        config.getExcludePath().add("C:\\ProgramData");
                    }else{
                        config.getExcludePath().add("/tmp");
                        config.getExcludePath().add("/etc");
                        config.getExcludePath().add("/root");
                    }
                }
            }
        }
        return config;
    }

    public static void main(String[] args) {
        EverythingPlusConfig config = EverythingPlusConfig.getInstance();
        System.out.println(config.getExcludePath());
        System.out.println(config.getIncludePath());
    }

}
