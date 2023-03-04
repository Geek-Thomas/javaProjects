package com.thomas.dao.user;

import com.thomas.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 16:49
 * @Description 用户接口类
 * @Since version-1.0
 */

public interface UserDao {
    //得到要登录的用户
    public User getLoginUser(Connection connection, String userCode) throws SQLException;

    //修改用户密码
    public int modifyPassword(Connection connection, int id, String newPassword) throws SQLException;

    //根据用户名或用户角色获取用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException;

    //获取用户列表
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException;

    //根据用户id查询用户信息
    public User getUserById(Connection connection, int id) throws SQLException;

    //增加用户
    public int addUser(Connection connection, User user) throws SQLException;

    //删除用户
    public int deleteUserById(Connection connection, int userId) throws SQLException;

    //修改用户
    public int modifyUser(Connection connection, User user) throws SQLException;
}
