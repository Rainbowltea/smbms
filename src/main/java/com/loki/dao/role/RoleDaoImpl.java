package com.loki.dao.role;

import com.loki.dao.BaseDao;
import com.loki.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    public List<Role> getRoleList(Connection connection) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        ArrayList<Role> roles = new ArrayList<>();
        if (connection != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            resultSet = BaseDao.execute(connection, sql, params, pstm, resultSet);
            while (resultSet.next()) {
                Role role = new Role();
                role.setId(resultSet.getLong("id"));
                role.setRoleCode(resultSet.getString("roleCode"));
                role.setRoleName(resultSet.getString("roleName"));
                roles.add(role);
            }
            BaseDao.closeResource(connection,pstm,resultSet);
        }
        return roles;
    }
}
