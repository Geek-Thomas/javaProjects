package com.thomas.service.user;

import com.thomas.dao.BaseDao;
import com.thomas.dao.user.UserDao;
import com.thomas.dao.user.UserDaoImpl;
import com.thomas.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 17:33
 * @Description 实现用户service接口
 * @Since version-1.0
 */

public class UserServiceImpl implements UserService {
    //业务层会调用dao层，所以需要引入dao
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/12 17:33
     * @Description 业务层调用dao层，获取登录的用户，并对用户名密码进行验证
     * @Param userCode
     * @Param password
     * @Return {@link User}
     * @Since version-1.0
     */

    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体的数据库操作
            user = userDao.getLoginUser(connection, userCode);
            //验证用户名和密码，如果未查询到user或者密码错误，则返回空
            if (user == null || !password.equals(user.getUserPassword())) {
                user = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return user;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/14 18:36
     * @Description 业务层调用dao层，判断是否更新成功，如果更新失败，业务层需要回滚事务
     * @Param id
     * @Param password
     * @Return {@link boolean}
     * @Since version-1.0
     */
    
    public boolean modifyPassword(int id, String password) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            int i = userDao.modifyPassword(connection, id, password);
            if (i > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/17 23:10
     * @Description 根据用户名或者用户角色进行查询，获取总数，也就是所有页面数据的总条数
     * @Param username
     * @Param userRole
     * @Return {@link int}
     * @Since version-1.0
     */

    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return count;
    }

    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> users = new ArrayList<User>();
        System.out.println("queryUserName ---> " + username);
        System.out.println("queryUserRole ---> " + userRole);
        System.out.println("queryCurrentPageNo ---> " + currentPageNo);
        System.out.println("queryPageSize ---> " + pageSize);
        try {
            connection = BaseDao.getConnection();
            users = userDao.getUserList(connection, username, userRole, currentPageNo, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return users;
    }

    public User getUserById(int id)  {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return user;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/20 22:16
     * @Description 调用dao层代码，添加新用户
     * @Param user
     * @Return {@link int}
     * @Since version-1.0
     */

    public boolean addUser(User user) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false); //开启jdbc事务管理
            int updatedRows = userDao.addUser(connection, user);
            connection.commit();
            if (updatedRows > 0) {
                flag = true;
                System.out.println("add user succeed");
            } else {
                System.out.println("add user failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //事务回滚
            try {
                System.out.println("rollback-------->");
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/21 0:50
     * @Description 通过用户编码查询用户
     * @Param userCode
     * @Return {@link User}
     * @Since version-1.0
     */

    public User selectUserCodeExist(String userCode) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return user;
    }

    public boolean deleteUserById(int userId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            int updatedRows = userDao.deleteUserById(connection, userId);
            if (updatedRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    public boolean modifyUser(User user) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            int updateRows = userDao.modifyUser(connection, user);
            if (updateRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    @Test
    public void testGetUserList() {
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.getUserById(7);
        System.out.println(user.getUserName() + ": " + user.getUserRoleName());
    }
}
