package com.thomas.dao.user;

import com.mysql.cj.util.StringUtils;
import com.thomas.dao.BaseDao;
import com.thomas.pojo.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 16:50
 * @Description 实现用户接口UserDao
 * @Since version-1.0
 */

public class UserDaoImpl implements UserDao {
    /**
     * @Author GeekThomas
     * @Date 2023/1/12 16:50
     * @Description 在数据库中根据userCode查询登录用户
     * @Param connection
     * @Param userCode
     * @Return {@link User}
     * @Since version-1.0
     */

    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object[] params = {userCode};

        User user = null;

        //如果拿到了数据库连接
        if (connection != null) {
            String sql = "select * from smbms_user where userCode = ?;";
            rs = BaseDao.execute(connection, ps, rs, sql, params);

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(userCode);
                user.setUserPassword(rs.getString("userPassword"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResources(null, ps, rs);
        }
        return user;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/14 18:34
     * @Description 修改数据库中用户密码
     * @Param connection
     * @Param id
     * @Param newPassword
     * @Return {@link int}
     * @Since version-1.0
     */

    public int modifyPassword(Connection connection, int id, String newPassword) throws SQLException {
        PreparedStatement ps = null;
        int execute = 0;

        if (connection != null) {
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object params[] = {newPassword, id};
            execute = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }

        return execute;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/17 22:40
     * @Description 根据用户名或者用户角色获取用户总数，需要拼接sql，动态添加参数！！！
     * @Param connection
     * @Param username
     * @Param userRole
     * @Return {@link int}
     * @Since version-1.0
     */

    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as cnt from smbms_user u, smbms_role r where u.userRole = r.id");

            //用一个可变长度的List来存储动态的参数
            ArrayList<Object> paramList = new ArrayList<Object>();

            //输入用户名，则在where条件里添加条件，注意空格
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                //"select count(1) as cnt from smbms_user u, smbms_role r
                // where u.userRole = r.id
                // and u.userName like %李%
                // "
                //参数实际上应该是%username%
                paramList.add("%" + username + "%");
            }

            //输入角色，则在where条件里添加条件，注意空格
            if (userRole > 0) {
                sql.append(" and r.id = ?");
                //"select count(1) as cnt from smbms_user u, smbms_role r
                // where u.userRole = r.id
                // and r.useRole = 1
                // "
                //参数实际上应该是userRole
                paramList.add(userRole);
            }

            //把List转为数组
            Object[] params = paramList.toArray();

            System.out.println("UserDaoImpl--->getUserCount:\n" + sql.toString()); //输出完整的sql
            rs = BaseDao.execute(connection, ps, rs, sql.toString(), params);

            if (rs.next()) {
                count = rs.getInt("cnt"); //从结果集中获取数量
            }

            //关闭资源
            BaseDao.closeResources(null, ps, rs);
        }

        return count;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/17 23:44
     * @Description 获取用户列表（可能会需要根据用户名，用户角色筛选）
     * @Param connection
     * @Param username
     * @Param userRole
     * @Param currentPageNo
     * @Param pageSize
     * @Return {@link List< User>}
     * @Since version-1.0
     */

    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        //存储分页查询的用户列表
        ArrayList<User> users = new ArrayList<User>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*, r.roleName from smbms_user u, smbms_role r where u.userRole = r.id");
            List<Object> paramList = new ArrayList<Object>();

            //增加用户名字的筛选
            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append(" and u.userName like ?");
                paramList.add("%" + username + "%");
            }

            //增加用户角色的筛选
            if (userRole > 0) {
                sql.append(" and r.id = ?");
                paramList.add(userRole);
            }

            //实现分页的功能
            sql.append(" order by u.creationDate limit ?, ?");
            //当前页面的起始记录，如果页面容量为5，那么就是
            //limit 0, 5(第一页)
            //limit 5, 5(第二页)
            int currentNo = (currentPageNo - 1) * pageSize;
            paramList.add(currentNo);
            paramList.add(pageSize);

            Object[] params = paramList.toArray();
            System.out.println("sql ---> " + sql.toString());
            rs = BaseDao.execute(connection, ps, rs, sql.toString(), params);

            while (rs.next()) {
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserPassword(rs.getString("userPassword"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setAddress(rs.getString("address"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setCreatedBy(rs.getInt("createdBy"));
                _user.setCreationDate(rs.getTimestamp("creationDate"));
                _user.setModifyBy(rs.getInt("modifyBy"));
                _user.setModifyDate(rs.getTimestamp("modifyDate"));
                _user.setUserRoleName(rs.getString("roleName"));
                users.add(_user);
            }

            BaseDao.closeResources(null, ps, rs);
        }
        return users;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/20 12:08
     * @Description 根据用户Id查询用户
     * @Param connection
     * @Param id
     * @Return {@link User}
     * @Since version-1.0
     */

    public User getUserById(Connection connection, int id) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        if (connection != null) {
            String sql = "select u.*, r.roleName from smbms_user u, smbms_role r where u.userRole = r.id and u.id = ?";
            Object[] params = {id};
            rs = BaseDao.execute(connection, ps, rs, sql, params);

            if (rs.next()) {
                user = new User();
                user.setId(id);
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRoleName(rs.getString("roleName"));
            }
            BaseDao.closeResources(null, ps, rs);
        }
        return user;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/20 22:06
     * @Description 增加用户，如果新增成功，则返回1
     * @Param connection
     * @Param user
     * @Return {@link int}
     * @Since version-1.0
     */

    public int addUser(Connection connection, User user) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "insert into smbms_user(userCode, userName, userPassword, gender, birthday, phone, address, " +
                    "userRole, createdBy, creationDate) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(), user.getGender(),
                    user.getBirthday(), user.getPhone(), user.getAddress(), user.getUserRole(), user.getCreatedBy(),
                    user.getCreationDate()};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/21 10:31
     * @Description 根据用户id删除用户
     * @Param connection
     * @Param userId
     * @Return {@link int}
     * @Since version-1.0
     */

    public int deleteUserById(Connection connection, int userId) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "delete from smbms_user where id = ?";
            Object[] params = {userId};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }

        return updatedRows;
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/21 15:47
     * @Description 修改用户信息
     * @Param connection
     * @Param user
     * @Return {@link int}
     * @Since version-1.0
     */
    public int modifyUser(Connection connection, User user) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "update smbms_user\n" +
                    "set userName=?,gender=?,birthday=?,phone=?,address=?,userRole=? where id = ?";
            Object[] params = {user.getUserName(), user.getGender(), user.getBirthday(), user.getPhone(), user.getAddress(),
                    user.getUserRole(), user.getId()};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }
}
