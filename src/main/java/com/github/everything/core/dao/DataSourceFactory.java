package com.github.everything.core.dao;

import com.sun.xml.internal.bind.v2.util.DataSourceSource;

import javax.activation.DataSource;

/**
 * 创建数据源 的 单例工厂类
 */
public class DataSourceFactory {
    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;


    private DataSourceFactory(){

    }
    public static DataSource dataSource(){
        if(dataSouce == null){
            synchronized (DataSourceSource.class){
                if(dataSouce == null){

                }
            }
        }
    }

}
