package com.github.everything.config;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.everything.core.search.FileSearch;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 配置类:对一些特定的文件或者目录进行排除，换句话说，搜索想要的，不搜索不想要的。
 */
@Getter//使得这里的数据只可以获取不可以修改
@ToString
public class EverythingPlusConfig {

    private static volatile EverythingPlusConfig config;

    //私有这个类
    private EverythingPlusConfig(){
        //错误，会引起空指针，因为我们在没有对象前在类中也需要使用初始化。
//        this.initDefaultPathConfig();//每次使用这个类肯定要先初始化，所以直接在这调用
    }

    //建立索引文件的路径
    private Set<String> includePath = new HashSet<>();

    //排除索引文件的路径
    private Set<String> excludePath = new HashSet<>();

    //TODO 可配置的参数会在这里体现
    /**
     * 检索返回的最大结果数
     */
    @Setter
    private Integer maxReturnNum = 30;

    /**
     * 深度排序的规则,默认是升序
     */
    @Setter
    private Boolean depthOrderAsc = true;

    /**
     * h2数据库文件路径
     */
    //获取当前工程路径。“user.home”获取文件路径
    private String h2IndexPath = System.getProperty("user.dir") + File.separator
            + "everything_plus";

    //获得初始化默认的系统，
    private void initDefaultPathConfig(){
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        /**
         * 遍历的目录
         */
        //Iterable<Path> iterable = fileSystem.getRootDirectories();
        //iterable.forEach(path -> config.includePath.add(path.toString()));
        config.includePath.add("D:\\临时文件");
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

    //单例模式
    public static EverythingPlusConfig getInstance(){
        if(config == null){
            synchronized (EverythingPlusConfig.class){
                if(config == null){
                    config = new EverythingPlusConfig();
                    config.initDefaultPathConfig();//解决错误，直接创建对象的时候初始化。
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
