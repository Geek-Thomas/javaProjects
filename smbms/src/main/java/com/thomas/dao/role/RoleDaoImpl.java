package com.thomas.dao.role;

import com.thomas.dao.BaseDao;
import com.thomas.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author GeekThomas
 * @Date 2023/1/19 20:49
 * @Description 实现角色接口RoleDao
 * @Since version-1.0
 */

public class RoleDaoImpl implements RoleDao {
    /**
     * @Author GeekThomas
     * @Date 2023/1/19 20:50
     * @Description 获得用户角色列表
     * @Param connection
     * @Param userRole
     * @Return {@link List< Role>}
     * @Since version-1.0
     */

    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Role> roles = new ArrayList<Role>();

        if (connection != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.execute(connection, ps, rs, sql, params);
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roles.add(role);
            }
            BaseDao.closeResources(null, ps, rs);
        }
        return roles;
    }
}
