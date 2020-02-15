package com.github.everything.DB;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.everything.config.EverythingPlusConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 创建数据源 的 单例工厂类
 */
public class DataSourceFactory {
    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {
    }

    //    建立数据源
    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC 的第一步 连接数据库
                    dataSource.setDriverClassName("org.h2.Driver");

                    /*url，URLname，password*/
                    //采用H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供URL接口
                    //JDBC规范中的关于Mysql  jdbc:mysql://ip:port/databaseName

                    //JDBC规范中关于H2  jdbc:h2:filepath  ->存储到本地文件
                    //JDBC规范中关于H2  jdbc:h2:~/filepath  ->存储到当前用户的home目录。
                    //JDBC规范中的关于H2  jdbc:h2://ip:port/databaseName  ->存储到服务器。
                    dataSource.setUrl("jdbc:h2:" + EverythingPlusConfig.getInstance()
                            .getH2IndexPath());

                    //Druid数据库的配置信息
                    //第一种
                    dataSource.setValidationQuery("select now()");
                    //第二种
                    //dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }


    //初始化数据库
    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2.获取SQL语句
        /*
        不使用绝对路径的方式读取文件，因为换一个主机路径就不一样了
         */
        //采用classpath路径下的文件

        //jdk 的优化 ：try（sql命令） 的方式创建命令就不用最后关闭，会自动关闭
        try (InputStream in = DataSourceFactory.class.getClassLoader().
                getResourceAsStream("everything_plus.sql")) {

            if (in == null) {
                throw new RuntimeException("无法初始化数据库，请检查");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String len = null;
                while ((len = reader.readLine()) != null) {
                    //不读取注释部分
                    if (!len.startsWith("--")) {//String.startsWith(String x) 判断是否已字符串“x”为开头
                        sqlBuilder.append(len);
                    }
                }
            }
            //3.获取数据库连接和执行sql语句
            String sql = sqlBuilder.toString();
            //JDBC
            //3.1获取数据库连接（二种方式，DriverManager 和 dataSource）
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3执行SQL语句
            statement.execute();
            //3.4关闭连接
            statement.close();
            connection.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }


    }

    //测试
    public static void main(String[] args) {
//        //演示IO包的使用
//        try(InputStream in  = DataSourceFactory.class.getClassLoader()
//                .getResourceAsStream("everything_plus.sql")){
////            String sql = IOUtils.toString(in);
////            System.out.println(sql);
//            IOUtils.readLines(in).stream().filter(new Predicate<String>() {
//                @Override
//                public boolean test(String line) {
//                    return !line.startsWith("--");
//                }
//            }).forEach(line -> System.out.println(line));
//        }catch(IOException e){
//
//        }
        //initDatabase();
        DataSource db = dataSource();
        try {
            Connection connection = db.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        initDatabase();
        System.out.println("jdbc:h2:" + EverythingPlusConfig.getInstance()
                .getH2IndexPath());
    }
}