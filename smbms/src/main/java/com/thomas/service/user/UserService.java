package com.thomas.service.user;

import com.thomas.pojo.User;

import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 17:31
 * @Description 用户业务类
 * @Since version-1.0
 */

public interface UserService {
    public User login(String userCode, String password);

    public boolean modifyPassword(int id, String password);

    public int getUserCount(String username, int userRole);

    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize);

    public User getUserById(int id);

    public boolean addUser(User user);

    public User selectUserCodeExist(String userCode);

    public boolean deleteUserById(int userId);

    public boolean modifyUser(User user);
}
