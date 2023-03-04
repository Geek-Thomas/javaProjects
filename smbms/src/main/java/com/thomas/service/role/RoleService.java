package com.thomas.service.role;

import com.thomas.pojo.Role;

import java.sql.SQLException;
import java.util.List;

public interface RoleService {
    public List<Role> getRoleList() throws SQLException;
}
