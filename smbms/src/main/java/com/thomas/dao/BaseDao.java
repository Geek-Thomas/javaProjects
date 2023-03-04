package com.thomas.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 13:30
 * @Description 操作数据库的公共类
 * @Since version-1.0
 */
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    
    //静态代码块，类加载的时候就初始化了
    static {
        Properties prop = new Properties();
        //通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = prop.getProperty("driver");
        url = prop.getProperty("url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/12 13:39
     * @Description 获取数据库的连接
     * @Param
     * @Return {@link Connection}
     * @Since version-1.0
     */

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/12 13:45
     * @Description 查询公共方法
     * @Param connection
     * @Param preparedStatement
     * @Param resultSet
     * @Param sql
     * @Param params 查询sql中的参数
     * @Return {@link ResultSet}
     * @Since version-1.0
     */

     public static ResultSet execute(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet, String sql, Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //占位符从1开始，数组从0开始！
            preparedStatement.setObject(i + 1, params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    
    /**
     * @Author GeekThomas
     * @Date 2023/1/12 13:52
     * @Description 编写增删改的公共方法
     * @Param connection
     * @Param preparedStatement
     * @Param sql
     * @Param params
     * @Return {@link int}
     * @Since version-1.0
     */

    public static int execute(Connection connection, PreparedStatement preparedStatement, String sql, Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            //占位符从1开始，数组从0开始！
            preparedStatement.setObject(i + 1, params[i]);
        }
        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/12 13:58
     * @Description 关闭资源
     * @Param preparedStatement
     * @Param resultSet
     * @Return {@link boolean}
     * @Since version-1.0
     */

    public static boolean closeResources(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        boolean flag = true;

        if (resultSet != null) {
            try {
                resultSet.close();
                //GC回收
                resultSet = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                //GC回收
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        if (connection != null) {
            try {
                connection.close();
                //GC回收
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }
}
