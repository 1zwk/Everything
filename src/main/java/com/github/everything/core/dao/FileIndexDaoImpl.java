package com.github.everything.core.dao;

import com.github.everything.core.util.PinyinUtil;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
//                String sql = "select name, path,depth,file_type,last_modified from file_index";

                //name      :  like 模糊匹配
                //filetype  :  =    精准匹配
                //limit     :
                //orderByAsc : order by
                StringBuilder sqlBuilder = new StringBuilder();
                //name匹配：前模糊，后模糊，前后模糊。
                sqlBuilder.append("SELECT\n" +
                        "\tid,\n" +
                        "\tNAME,\n" +
                        "\tfile_size,\n" +
                        "\tpath,\n" +
                        "\tdepth,\n" +
                        "\tfile_type,\n" +
                        "\tlast_modified \n" +
                        "FROM\n" +
                        "\tfile_index \n" +
                        "WHERE\n" +
                        "\tNAME LIKE '%").append(condition.getName())
                        .append("%' UNION\n" +
                                "SELECT\n" +
                                "\tid,\n" +
                                "\tNAME,\n" +
                                "\tfile_size,\n" +
                                "\tpath,\n" +
                                "\tdepth,\n" +
                                "\tfile_type,\n" +
                                "\tlast_modified \n" +
                                "FROM\n" +
                                "\tfile_index \n" +
                                "WHERE\n" +
                                "\tid in ( SELECT DISTINCT file_id FROM file_pinyin WHERE pinyin LIKE '%")
                        .append(condition.getName().toLowerCase())
                        .append("%' )");
                //类型匹配
                if (condition.getFileType() != null) {
                    sqlBuilder.append(" and file_type =' ")
                            .append(condition.getFileType()
                                    .toUpperCase())
                            .append(" ' ");//因为我们设置的是大写，所以把输入转为大写。
                }
                //order ，根据condition的true或者false判断
                if (condition.getOrderByAsc() != null) {
                    sqlBuilder.append(" order by depth ")
                            .append(condition.getOrderByAsc() ? "asc" : "desc");
                }
                //分页查询limit n offset m ,从m开始查询，筛选n条结果。
                if (condition.getLimit() != null) {
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
                while (resultSet.next()) {
                    //把数据库中的行 ——> 为java中的对象
                    Thing thing = new Thing();
                    thing.setId(resultSet.getInt("id"));
                    thing.setFileSize(resultSet.getString("file_size"));
                    thing.setName(resultSet.getString("name"));
                    thing.setPath(resultSet.getString("path"));
                    thing.setDepth(resultSet.getInt("depth"));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");
                    thing.setLast_modified(sdf.format(resultSet.getLong("last_modified")));
                    //处理枚举
                    String fileType = resultSet.getString("file_type");
                    thing.setFileType(FileType.lookupByName(fileType));

//                    thing.setFileSize(resultSet.getString("file_size"));

                    //add返回结果List
                    things.add(thing);
                }

            } finally {
                releaseResource(resultSet, statement, connection);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return things;
    }


    @Override
    //插入Thing
    public void insert(Thing thing) {
        String sqlPinyin = "insert into file_pinyin(pinyin,file_id) values(?,?)";
        String sqlFile = "insert into file_index( name, path, depth, file_type, file_size, last_modified,id) values(?,?,?,?,?,?,?)";
        insertFile(thing,sqlFile,sqlPinyin);

//        insertFile(thing, sqlFile);
    }

    public void insertFile(Thing thing,String sqlFile,String sqlPinyin) {
        Connection connection = null;
        PreparedStatement statement = null;

        List<String> list = PinyinUtil.getPinyin(thing.getName());
        System.out.println(list);

        try {
            try {
                //1.获取数据库连接
                connection = dataSource.getConnection();
                //2.准备SQL语句
                connection.setAutoCommit(false);//取消自动提交

                //3.准备命令
                statement = connection.prepareStatement(sqlFile/*,PreparedStatement.RETURN_GENERATED_KEYS*/);
                //4.设置参数1 2 3 4
                statement.setString(1, thing.getName());
                statement.setString(2, thing.getPath());
                statement.setInt(3, thing.getDepth());
                //枚举 FileType.DOC -> DOC
                statement.setString(4, thing.getFileType().name());

                statement.setString(5, thing.getFileSize());
                statement.setString(6, thing.getLast_modified());

//                ResultSet resultSet = statement.getGeneratedKeys();
//                resultSet.next();
//                int id = resultSet.getInt(1);
//                System.out.println(id);

                statement.setInt(7,thing.getId());

                //5.执行命令
                statement.execute();
                statement = connection.prepareStatement(sqlPinyin);

                for (String s : list) {
                    //4.设置参数1 2 3 4
                    statement.setString(1, s);
                    statement.setInt(2, thing.getId());
                    System.out.print( s);
                    //5.执行命令
                    statement.execute();
                }

                connection.commit();
            } finally {
                releaseResource(null, statement, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    //解决每次关闭代码大量重复 ：
    private void releaseResource(ResultSet resultSet
            , PreparedStatement preparedStatement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
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
                 * TODO 问题：没懂这个以path为前缀的文件都删除
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


}
