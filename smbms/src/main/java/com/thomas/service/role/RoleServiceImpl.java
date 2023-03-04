package com.thomas.service.role;

import com.alibaba.fastjson.JSONArray;
import com.thomas.dao.BaseDao;
import com.thomas.dao.role.RoleDao;
import com.thomas.dao.role.RoleDaoImpl;
import com.thomas.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/19 21:08
 * @Description 实现RoleService接口
 * @Since version-1.0
 */

public class RoleServiceImpl implements RoleService {
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/19 21:08
     * @Description 调用RoleDao，实现查询用户角色列表
     * @Param
     * @Return {@link List< Role>}
     * @Since version-1.0
     */

    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roles = new ArrayList<Role>();
        try {
            connection = BaseDao.getConnection();
            roles = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return roles;
    }

    @Test
    public void testRoleServiceImpl() throws SQLException {
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        String s = JSONArray.toJSONString(roleList);
        System.out.println(s);
        for (Role role : roleList) {
            System.out.println("role: " + role.getId() + " ---> " + role.getRoleName());
        }
    }
}
