//package com.github.everything.core.dao.impl;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.github.everything.config.EverythingPlusConfig;
//import com.github.everything.core.dao.DataSourceFactory;
//
//import javax.sql.DataSource;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//import static java.lang.Class.forName;
//
//public class Test {
//    private static volatile D dataSource;
//
//    public static DataSource dataSource() {
//        if (dataSource == null) {
//            synchronized (DataSourceFactory.class) {
//                if (dataSource == null) {
//                    //实例化
//                    dataSource = new DruidDataSource();
//                    //JDBC 的第一步 连接数据库
//                    dataSource.set
//
//                    /*url，URLname，password*/
//                    //采用H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供URL接口
//                    //JDBC规范中的关于Mysql  jdbc:mysql://ip:port/databaseName
//
//                    //JDBC规范中关于H2  jdbc:h2:filepath  ->存储到本地文件
//                    //JDBC规范中关于H2  jdbc:h2:~/filepath  ->存储到当前用户的home目录。
//                    //JDBC规范中的关于H2  jdbc:h2://ip:port/databaseName  ->存储到服务器。
//                    dataSource.setUrl("jdbc:h2:" + EverythingPlusConfig.getInstance()
//                            .getH2IndexPath());
//
//                    //Druid数据库的配置信息
//                    //第一种
//                    dataSource.setValidationQuery("select now()");
//                    //第二种
//                    //dataSource.setTestWhileIdle(false);
//                }
//            }
//        }
//        return dataSource;
//    }
//
//    public static void main(String[] args) {
//        DataSource db = dataSource();
//        try {
//            Connection connection = db.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void initDatabase()  {
//        //1.获取数据源
//        DataSource dataSource = DataSourceFactory.dataSource();
//        //2.获取SQL语句
//        /*
//        不使用绝对路径的方式读取文件，因为换一个主机路径就不一样了
//         */
//        //采用classpath路径下的文件
//
//        //jdk 的优化 ：try（sql命令） 的方式创建命令就不用最后关闭，会自动关闭
//        String sql = "drop table if exists  file_index;\n" +
//                " create table if not exists file_index\n" +
//                " (\n" +
//                "     id int primary key auto_increment,\n" +
//                "     name varchar(256) not null comment '文件名称',\n" +
//                "     path varchar(2048) not null comment '文件路径',\n" +
//                "     depth int not null comment '文件路径深度',\n" +
//                "     file_type varchar (32) not null comment '文件类型',\n" +
//                "     file_size varchar(10) not null comment '文件大小',\n" +
//                "     last_modified varchar(32) not null comment '最后改动时间'\n" +
//                " );\n" +
//                "\n" +
//                " drop table if exists  file_pinyin;\n" +
//                " create table if not exists file_pinyin\n" +
//                " (\n" +
//                "     pinyin varchar(256) not null comment '文件拼音',\n" +
//                "     file_id int,\n" +
//                "     foreign key (file_id) references file_index(id)\n" +
//                " );";
//            //3.获取数据库连接和执行sql语句
//
//            //JDBC
//            //3.1获取数据库连接（二种方式，DriverManager 和 dataSource）
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            //3.2创建命令
//            PreparedStatement statement = connection.prepareStatement(sql);
//            //3.3执行SQL语句
//            statement.execute();
//            //3.4关闭连接
//            statement.close();
//            connection.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//
//
//    }
//}
