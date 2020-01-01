package com.github.everything.core.dao.impl;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {

    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }



    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            try {
                //1.获取数据库连接
                connection = dataSource.getConnection();
                //2.准备SQL语句
                String sql = "select name, path,depth,file_type from file_index";

                //name      :  like 模糊匹配
                //filetype  :  =    精准匹配
                //limit     :
                //orderByAsc : order by
                StringBuilder sqlBuilder = new StringBuilder();
                //name匹配：前模糊，后模糊，前后模糊。
                sqlBuilder.append(" select name, path,depth,file_type from file_index ");
                sqlBuilder.append(" where ").append(" name like '%")
                                    .append(condition.getName())
                                    .append("%' ");
                //类型匹配
                if(condition.getFileType() != null){
                    sqlBuilder.append(" and file_type =' ")
                                    .append(condition.getFileType()
                                    .toUpperCase())
                                    .append(" ' ");//因为我们设置的是大写，所以把输入转为大写。
                }
                //order ，根据condition的true或者false判断
                if(condition.getOrderByAsc() != null) {
                    sqlBuilder.append(" order by depth ")
                            .append(condition.getOrderByAsc() ? "asc" : "desc");
                }
                //分页查询limit n offset m ,从m开始查询，筛选n条结果。
                if(condition.getLimit() != null){
                    sqlBuilder.append(" limit ").append(condition.getLimit()).append(" offset 0 ");
                }
                //3.准备命令
                statement = connection.prepareStatement(sqlBuilder.toString());
                //4.设置参数1 2 3 4
                //无
                //5.执行命令
                /**
                 * 查询语句执行后返回需要用结果集接收！！不然处理个锤子，
                 */
                resultSet = statement.executeQuery();//查询是excuteQuery，更新，删除，插入为executeUpdate 。
                //6.处理结果
                while(resultSet.next()){
                    //把数据库中的行 ——> 为java中的对象
                    Thing thing = new Thing();
                    thing.setName(resultSet.getString("name"));
                    thing.setPath(resultSet.getString("path"));
                    thing.setDepth(resultSet.getInt("depth"));
                    //处理枚举
                    String fileType = resultSet.getString("file_type");
                    thing.setFileType(FileType.lookupByName(fileType));

                    //add返回结果List
                    things.add(thing);
                }

            } finally {
                releaseResource(resultSet,statement,connection);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return things;
    }

    @Override
    //插入Thing
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            try {
                //1.获取数据库连接
                connection = dataSource.getConnection();
                //2.准备SQL语句
                String sql = "insert into file_index(name, path, depth, file_type) values(?,?,?,?)";
                //3.准备命令
                statement = connection.prepareStatement(sql);
                //4.设置参数1 2 3 4
                statement.setString(1,thing.getName());
                statement.setString(2,thing.getPath());
                statement.setInt(3,thing.getDepth());
                //枚举 FileType.DOC -> DOC
                statement.setString(4,thing.getFileType().name());

                //5.执行命令
                statement.execute();
            } finally {
                releaseResource(null,statement,connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //解决每次关闭代码大量重复 ：
    private void releaseResource(ResultSet resultSet
            , PreparedStatement preparedStatement,Connection connection){
        try {
            if(resultSet != null){
                resultSet.close();
            }
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param thing
     */
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            try {
                //1.获取数据库连接
                connection = dataSource.getConnection();
                //2.准备SQL语句
                /**
                 * 优化：根据path删除文件，并且如果是目录的话，
                 * 我们在判断这个目录的最终指向文件为空，就把以这个path为前缀的文件都删除
                 *
                 * 问题：没懂这个以path为前缀的文件都删除
                 */
                String sql = "delete from file_index where path like '\" + thing.getPath() + \"%'";
                //3.准备命令
                statement = connection.prepareStatement(sql);
                //4.设置参数1 2 3 4
//                statement.setString(1,thing.getPath());
                //5.执行命令
                statement.executeUpdate();
            } finally {
                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
        Thing thing = new Thing();
        thing.setName("简历2.ppt");
        thing.setPath("D:\\a\\temp\\简历2.ppt");
        thing.setDepth(3);

        thing.setFileType(FileType.DOC);

//        fileIndexDao.insert(thing);

        Condition condition = new Condition();
        condition.setName("简历");
        condition.setLimit(10);
        condition.setOrderByAsc(true);
        condition.setFileType("IMP");
        List<Thing> list = fileIndexDao.search(condition);

        for(Thing t : list){
            System.out.println(t);
        }






    }
}
